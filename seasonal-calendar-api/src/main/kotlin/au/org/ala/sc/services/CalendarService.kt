package au.org.ala.sc.services

import au.org.ala.profiles.service.*
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.domain.jooq.tables.daos.CalendarDao
import au.org.ala.sc.domain.jooq.tables.pojos.Calendar
import au.org.ala.sc.domain.jooq.tables.Calendar.CALENDAR
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import java.util.*

private const val HTTP_NOT_FOUND = 404

class CalendarService(
    private val calendarDao: CalendarDao,
    private val ctx: DSLContext,
    private val seasonService: SeasonService,
    private val profilesServiceClient: ProfileServiceClient,
    private val defaultDataResourceUid: String) {

    companion object {
        private val log = LoggerFactory.getLogger(CalendarService::class.java)

        const val userId = "8373"

        const val CALENDAR_TAG = "IEK"
    }

    fun saveCalendar(calendar: SeasonalCalendarDto) {
        if (calendar.collectionUuid.isBlank()) {
            insertCalendar(calendar)
        } else {
            updateCalendar(calendar)
        }
    }

    fun updateCalendar(calendar: SeasonalCalendarDto) {
        ctx.transaction { txConfig ->
            val opusId = calendar.collectionUuid
            val opusUuid = UUID.fromString(opusId)
            val opus = getOpus(opusId)
            opus.populateOpusFromSeasonalCalendar(calendar)
            val updateOpusResponse = profilesServiceClient.updateOpus(opusId, userId, opus).execute()
            if (updateOpusResponse.isSuccessful && updateOpusResponse.body()!!.isSuccess) {
                val txCalendarDao = CalendarDao(txConfig)
                txCalendarDao.update(mapCalendarDtoToRecord(calendar))
                calendar.seasons.onEach { season ->
                    seasonService.saveSeason(opusUuid, season, txConfig)
                }
            } else {
                log.error("Couldn't update Opus for id {} with response {}", opusId, updateOpusResponse.code())
                throw CalendarException("Couldn't update Opus for id $opusId with response ${updateOpusResponse.code()}")
            }
        }
    }

    fun insertCalendar(calendar: SeasonalCalendarDto): UUID {
        return ctx.transactionResult { txConfig ->
            val createOpusResponse = profilesServiceClient.createOpus(userId, opusFromSeasonalCalendar(calendar)).execute()

            if (createOpusResponse.isSuccessful) {
                val opus = createOpusResponse.body()!!
                val opusId = opus.uuid
                val opusUuid = UUID.fromString(opus.uuid)
                val record = mapCalendarDtoToRecord(calendar.copy(collectionUuid = opusId))
                try {
                    val txCalendarDao = CalendarDao(txConfig)
                    txCalendarDao.insert(record)
                    calendar.seasons.forEach {
                        seasonService.saveSeason(opusUuid, it, txConfig)
                    }
                    return@transactionResult opusUuid
                } catch (e: Exception) {
                    log.error("Couldn't insert calendar record {}", record, e)
                    if (!profilesServiceClient.deleteOpus(opusId, userId).execute().isSuccessful) {
                        log.error("Couldn't delete corresponding Opus!")
                    }
                    throw e
                }
            } else {
                log.error("Couldn't create opus with code {}: ", createOpusResponse.code(), createOpusResponse.errorBody()?.string())
                throw CalendarException("Couldn't create opus")
            }
        }
    }

    private fun getOpus(name: String): Opus {
        val getOpusResponse = profilesServiceClient.getOpus(name, userId).execute()
        if (getOpusResponse.isSuccessful) {
            return getOpusResponse.body()!!
        } else {
            if (getOpusResponse.code() == HTTP_NOT_FOUND) {
                log.error("Couldn't find opus with name $name")
                throw CalendarNotFoundException(name)
            } else {
                log.error("Couldn't find opus with name $name and error body ${getOpusResponse.errorBody()?.string()}")
                throw CalendarException("Error getting opus with name $name")
            }
        }
    }

    fun getSeasonalCalendar(name: String) : SeasonalCalendarDto {
        val opus = getOpus(name)
        val opusUuid = UUID.fromString(opus.uuid)

        val calendar = calendarDao.findById(opusUuid)
        return constructCalendarDto(opus, calendar).copy(seasons = seasonService.getSeasonsForCalendarId(opusUuid))
    }

    fun getSeasonalCalendars(publishedOnly: Boolean = true) : List<SeasonalCalendarDto> {
        val getOperaResponse = profilesServiceClient.getOperaByTag(CALENDAR_TAG, userId, emptyMap()).execute()
        if (getOperaResponse.isSuccessful) {
            val opera = getOperaResponse.body()!!
            val operaMap = opera.associateBy { UUID.fromString(it.uuid) }
            val calendars = calendarDao.fetchByCollectionUuid(*operaMap.keys.toTypedArray())
            val calendarsMap = calendars.associateBy { it.collectionUuid }
            val comboMap = operaMap.mapValues { it.value to calendarsMap[it.key] }
            val filteredComboMap = if (publishedOnly) {
                comboMap.filter { (_, pair) -> pair.second?.published ?: false }
            } else {
                comboMap
            }
            return filteredComboMap.map { (_, pair) -> constructCalendarDto(pair.first, pair.second) }
        } else {
            log.error("getOperaByTag failed with code {}, body: {}", getOperaResponse.code(), getOperaResponse.errorBody()?.string())
            throw CalendarException("getOperaByTag failed with code ${getOperaResponse.code()}")
        }
    }

    fun getTag(): Tag? {
        val response = profilesServiceClient.getTags(userId).execute()
        return if (response.isSuccessful) {
            response.body()!!.tags.find { it.abbrev == CALENDAR_TAG || it.name == CALENDAR_TAG || it.uuid == CALENDAR_TAG }
        } else {
            throw CalendarException("Couldn't get tags")
        }
    }

    private fun opusFromSeasonalCalendar(calendar: SeasonalCalendarDto) : Opus {
        val tag = getTag()
        if (tag == null) log.warn("No tag found matching $CALENDAR_TAG")
        return Opus().apply {
            dataResourceUid = defaultDataResourceUid
            isAutoApproveShareRequests = false
            isAutoDraftProfiles = false
            isKeepImagesPrivate = true
            isPrivateCollection = true
            isUsePrivateRecordData = false
            if (tag != null) {
                tags.add(tag)
            }
            populateOpusFromSeasonalCalendar(calendar)
        }
    }

    private fun Opus.populateOpusFromSeasonalCalendar(calendar: SeasonalCalendarDto): Opus {
        shortName = calendar.name
        title = calendar.name
        description = calendar.description
        opusLayoutConfig.images.clear()
        opusLayoutConfig.images.add(Image().apply {
            imageUrl = calendar.imageUrl
            credit = "CC-BY"
        })
        email = calendar.contactEmail // for insert
        contact.email = calendar.contactEmail // for update
        aboutHtml = calendar.about
        brandingConfig.logos.clear()
        brandingConfig.logos.add(Logo().apply {
            hyperlink = calendar.organisationUrl
            logoUrl = calendar.organisationLogoUrl
        })
        brandingConfig.shortLicense = calendar.licenceTerms
//            brandingConfig.pdfLicence = calendar.limitations // TODO where is the license long description
        mapConfig.mapDefaultLatitude = calendar.latitude?.toFloat()
        mapConfig.mapDefaultLongitude = calendar.longitude?.toFloat()
        mapConfig.mapZoom = calendar.zoom
        isPrivateCollection = true //!calendar.published
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

    fun constructCalendarDto(opus: Opus, calendar: Calendar?) : SeasonalCalendarDto {
        return SeasonalCalendarDto(
            collectionUuid = opus.uuid,
            shortName = opus.shortName ?: "",
            name = opus.title,
            description = opus.description ?: "",
            imageUrl = opus.opusLayoutConfig.images?.firstOrNull()?.imageUrl ?: "",
            websiteUrl = calendar?.website ?: "",
            youtubeId = calendar?.youtubeId ?: "",
            organisationName =  calendar?.organisationName ?: "",
            contributors = calendar?.contributors?.toList() ?: mutableListOf(),
            contactName = calendar?.contactName ?: "",
            contactEmail = opus.contact?.email ?: "",
            keywords = calendar?.keywords?.toList() ?: mutableListOf(),
            about = opus.aboutHtml ?: "",
            organisationUrl = opus.brandingConfig.logos?.firstOrNull()?.hyperlink ?: "",
            organisationLogoUrl = opus.brandingConfig.logos?.firstOrNull()?.logoUrl ?: "",
            development = calendar?.development ?: "",
            references = calendar?.references?.toList() ?: mutableListOf(),
            referenceLinks = calendar?.referenceLinks?.toList() ?: mutableListOf(),
            developmentReason = calendar?.developmentReason ?: "",
            limitations = "", // TODO
            licenceTerms = opus.brandingConfig?.shortLicense ?: "",
            latitude =  opus.mapConfig?.mapDefaultLatitude?.toDouble(),
            longitude = opus.mapConfig?.mapDefaultLongitude?.toDouble(),
            zoom = opus.mapConfig?.mapZoom,
            languageGroup = calendar?.language ?: "",
            published = calendar?.published ?: false
        )
    }

    fun publishSeasonalCalendar(calendarName: String, publish: Boolean = true) {
        val uuid = if (calendarName.isUuid()) {
            UUID.fromString(calendarName)
        } else {
            UUID.fromString(getOpus(calendarName).uuid)
        }
        val updates = ctx.update(CALENDAR).set(CALENDAR.PUBLISHED, publish).where(CALENDAR.COLLECTION_UUID.eq(uuid)).execute()
        if (updates == 0) {
            log.error("Attempted to publish Calendar $calendarName but 0 updates occured")
            throw CalendarNotFoundException(calendarName)
        } else if (updates > 1) {
            log.error("Too many updates ($updates) attempting to publish Calendar $calendarName")
        }
    }

    fun deleteCalendar(calendarName: String) {
        val uuid = if (calendarName.isUuid()) {
            UUID.fromString(calendarName)
        } else {
            UUID.fromString(getOpus(calendarName).uuid)
        }

        val deletes= ctx.delete(CALENDAR).where(CALENDAR.COLLECTION_UUID.eq(uuid)).execute()

        val deleteResponse = profilesServiceClient.deleteOpus(calendarName, userId).execute()
        if (!deleteResponse.isSuccessful) {
            val code = deleteResponse.code()
            if (code == HTTP_NOT_FOUND) {
                throw CalendarNotFoundException(calendarName)
            } else {
                log.error("Error deleting opus with name $calendarName, error text: ${deleteResponse.errorBody()?.string()}")
                throw CalendarException("Error deleting opus with name $calendarName")
            }
        }
    }
}

open class CalendarException(message: String) : RuntimeException(message)
class CalendarNotFoundException(val name: String) : CalendarException("Couldn't find a calendar for $name")

private val uuidv1 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[1][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv2 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[2][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv3 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[3][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv4 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[4][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuidv5 = Regex("^[0-9A-F]{8}-[0-9A-F]{4}-[5][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}\$", RegexOption.IGNORE_CASE)
private val uuids = listOf(uuidv4, uuidv1, uuidv2, uuidv3, uuidv5)

fun String?.isUuid() = this?.trim()?.let { v -> uuids.any { uuid -> uuid.matches(v) } } ?: false
