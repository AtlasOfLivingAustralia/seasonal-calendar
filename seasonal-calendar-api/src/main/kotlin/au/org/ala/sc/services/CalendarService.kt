package au.org.ala.sc.services

import au.org.ala.profiles.service.Image
import au.org.ala.profiles.service.Logo
import au.org.ala.profiles.service.Opus
import au.org.ala.profiles.service.ProfileServiceClient
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.domain.jooq.tables.daos.CalendarDao
import au.org.ala.sc.domain.jooq.tables.pojos.Calendar
import org.jooq.exception.DataAccessException
import org.slf4j.LoggerFactory
import java.util.*

class CalendarService(
    val calendarDao: CalendarDao,
    val seasonService: SeasonService,
    val profilesServiceClient: ProfileServiceClient) {

    companion object {
        private val log = LoggerFactory.getLogger(CalendarService::class.java)

        const val userId = "8373"
    }

    fun saveCalendar(calendar: SeasonalCalendarDto) {
        if (calendar.collectionUuid.isBlank()) {
            insertCalendar(calendar)
        } else {
            updateCalendar(calendar)
        }
    }

    private fun updateCalendar(calendar: SeasonalCalendarDto) {
        val opusId = calendar.collectionUuid
        val getOpusResponse = profilesServiceClient.getOpus(opusId, userId).execute()
        if (getOpusResponse.isSuccessful) {
            val opus = getOpusResponse.body()!!
            opus.populateOpusFromSeasonalCalendar(calendar)
            val updateOpusResponse = profilesServiceClient.updateOpus(opusId, userId, opus).execute()
            if (updateOpusResponse.isSuccessful && updateOpusResponse.body()!!.isSuccess) {
                calendarDao.update(mapCalendarDtoToRecord(calendar))
            } else {
                log.error("Couldn't update Opus for id {} with response {}", opusId, getOpusResponse.code())
                throw CalendarException("Couldn't update Opus for id $opusId with response ${getOpusResponse.code()}")
            }
        } else {
            log.error("Couldn't get Opus for id {} with response {}", opusId, getOpusResponse.code())
            throw CalendarException("Couldn't get Opus for id $opusId with response ${getOpusResponse.code()}")
        }
    }

    private fun insertCalendar(calendar: SeasonalCalendarDto) {
        val createOpusResponse = profilesServiceClient.createOpus(userId, opusFromSeasonalCalendar(calendar)).execute()

        if (createOpusResponse.isSuccessful) {
            val opus = createOpusResponse.body()!!
            val opusId = opus.uuid
            val opusUuid = UUID.fromString(opus.uuid)
            val record = mapCalendarDtoToRecord(calendar.copy(collectionUuid = opusId))
            try {
                calendarDao.insert(record)
                calendar.seasons.forEach {
                    seasonService.saveSeason(opusUuid, it)
                }
            } catch (e: Exception) {
                log.error("Couldn't insert calendar record {}", record, e)
                if (!profilesServiceClient.deleteOpus(opusId, userId).execute().isSuccessful) {
                    log.error("Couldn't delete corresponding Opus!")
                }
                throw e
            }
        } else {
            log.error("Couldn't create opus with code {}", createOpusResponse.code())
            throw CalendarException("Couldn't create opus with code ${createOpusResponse.code()}")
        }
    }

    fun getSeasonalCalendar(name: String) : SeasonalCalendarDto {
        val getOpusResponse = profilesServiceClient.getOpus(name, userId).execute()
        if (getOpusResponse.isSuccessful) {
            val opus = getOpusResponse.body()!!
            val opusUuid = UUID.fromString(opus.uuid)

            val calendar = calendarDao.findById(opusUuid)
            return constructCalendarDto(opus, calendar)
        } else {
            log.error("Couldn't find calendar with name $name")
            throw CalendarException("Couldn't find calendar with name $name")
        }
    }

    fun getSeasonalCalendars() : List<SeasonalCalendarDto> {
        val getOperaResponse = profilesServiceClient.getOperaByTag("IEK", userId, emptyMap()).execute()
        if (getOperaResponse.isSuccessful) {
            val opera = getOperaResponse.body()!!
            val operaMap = mapOf(*opera.map { UUID.fromString(it.uuid) to it }.toTypedArray())
            val calendars = calendarDao.fetchByCollectionUuid(*operaMap.keys.toTypedArray())
            val calendarsMap = mapOf(*calendars.map { it.collectionUuid to it }.toTypedArray())
            val comboMap = operaMap.mapValues { it.value to calendarsMap[it.key]!! }
            return comboMap.map { constructCalendarDto(it.value.first, it.value.second) }
        } else {
            log.error("getOperaByTag failed with code {}", getOperaResponse.code())
            throw DataAccessException("getOperaByTag failed with code ${getOperaResponse.code()}")
        }
    }

    fun opusFromSeasonalCalendar(calendar: SeasonalCalendarDto) : Opus {
        return Opus().apply {
            this.populateOpusFromSeasonalCalendar(calendar)
        }
    }

    fun Opus.populateOpusFromSeasonalCalendar(calendar: SeasonalCalendarDto): Opus {
        shortName = calendar.name
        title = calendar.name
        description = calendar.description
        opusLayoutConfig.images.add(Image().apply {
            imageUrl = ""
            credit = "CC-BY"
        })
        email = calendar.contactEmail
        aboutHtml = calendar.about
        brandingConfig.logos.add(Logo().apply {
            hyperlink = calendar.organisationUrl
            logoUrl = calendar.organisationLogoUrl
        })
        brandingConfig.shortLicense = calendar.licenceTerms
//            brandingConfig.pdfLicence = calendar.limitations // TODO where is the license long description
        mapConfig.mapDefaultLatitude = calendar.latitude.toFloat()
        mapConfig.mapDefaultLongitude = calendar.longitude.toFloat()
        isPrivateCollection = calendar.published
        return this
    }
    /*
Calendar field  |   Collection field |   Comments
Calendar Name |   Collection Name |   * Does this name need to be unique within Profiles (perhaps add Seasonal Calendar as suffix)
Calendar Description |   Collection Description |
Calendar Image URL |   Image Slider image |   * Noting Profiles allows multiple images (maybe Seasonal Calendars only uses the first image for now)
Calendar website |   Collection website |   New field, can be displayed with the collection email.
Promotional video |   Collection video |   new field, Include in Site overview section
Organisation Name |   Organisation name |   New field, Include in Site overview section
Contributors |   Collection Contributors |   New field, Include in Authorship and Attribution controls section
Contact Name |   Collection contact |   New field, Include in Site overview section
Contact Email |   Email Contact |
Keywords |   Keywords |   New field, Include in Site overview section
About |   Explanatory text |
Organization URL |   Logo and Link |   This is a link to the authoring organisation website rather than a link to calendar website
Organisation logo |   Logo and Link |   Profiles allows multiple logos/links
How the calendar was developed |   Development |   New field, Include in Seasonal Calendar section (Note: this would be a new section at the bottom of the Collection configuration page)
References |   References |   New field, Include in Seasonal Calendar section
Reference Links |   Reference links |   New field, Include in Seasonal Calendar section
Why the calendar was developed  |   Development reason |   New field, Include in Seasonal Calendar section
Limitations |   Licence – long description
License termss |   License – short description |   (note: misspelling of "terms" in Seasonal Calendars)
(map region) |   - |   Not sure how to do this (is it stored as a snapshot image or map location?)
(language group)
Unpublished or Published status |   Collection private or public |   When a seasonal calendar is unpublished, the supporting collection should be private.  When a seasonal collection is published, the supporting collection should automatically be made public.
*/
    fun mapCalendarDtoToRecord(calendar: SeasonalCalendarDto) : Calendar {
        return Calendar(
            UUID.fromString(calendar.collectionUuid),
            calendar.websiteUrl,
            calendar.youtubeId,
            calendar.organisationName,
            calendar.contributors.toTypedArray(),
            calendar.contactName,
            calendar.keywords.toTypedArray(),
            calendar.development,
            calendar.developmentReason,
            calendar.references.toTypedArray(),
            calendar.referenceLinks.toTypedArray(),
            calendar.languageGroup,
            calendar.published
        )
    }

    fun constructCalendarDto(opus: Opus, calendar: Calendar) : SeasonalCalendarDto {
        return SeasonalCalendarDto(
            collectionUuid = opus.uuid,
            shortName = opus.shortName,
            name = opus.title,
            description = opus.description,
            imageUrl = opus.opusLayoutConfig.images?.firstOrNull()?.imageUrl ?: "",
            websiteUrl = calendar.website,
            youtubeId = calendar.youtubeId,
            organisationName =  calendar.organisationName,
            contributors = calendar.contributors.toList(),
            contactName = calendar.contactName,
            contactEmail = opus.email,
            keywords = calendar.keywords.toList(),
            about = opus.aboutHtml,
            organisationUrl = opus.brandingConfig.logos?.firstOrNull()?.hyperlink ?: "",
            organisationLogoUrl = opus.brandingConfig.logos?.firstOrNull()?.logoUrl ?: "",
            development = calendar.development,
            references = calendar.references.toList(),
            referenceLinks = calendar.referenceLinks.toList(),
            developmentReason = calendar.developmentReason,
            limitations = "", // TODO
            licenceTerms = opus.brandingConfig.shortLicense,
            latitude =  opus.mapConfig.mapDefaultLatitude.toDouble(),
            longitude = opus.mapConfig.mapDefaultLongitude.toDouble(),
            zoom = opus.mapConfig.mapZoom,
            languageGroup = calendar.language,
            published = calendar.published
        )
    }
}

class CalendarException(message: String) : RuntimeException(message)