package au.org.ala.sc.services

import arrow.syntax.collections.tail
import arrow.syntax.function.partially2
import au.org.ala.profiles.service.*
import au.org.ala.sc.api.CalendarUser
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.auth.CALENDAR_ADMIN_ROLES
import au.org.ala.sc.auth.ROLE_CALENDAR_ADMIN
import au.org.ala.sc.auth.ROLE_CALENDAR_EDITOR
import au.org.ala.sc.auth.User
import au.org.ala.sc.domain.jooq.tables.daos.CalendarDao
import au.org.ala.sc.domain.jooq.tables.pojos.Calendar
import au.org.ala.sc.domain.jooq.tables.Calendar.CALENDAR
import au.org.ala.sc.domain.jooq.tables.Role.ROLE
import au.org.ala.sc.domain.jooq.tables.UserRole.USER_ROLE
import au.org.ala.sc.domain.jooq.tables.daos.RoleDao
import au.org.ala.sc.domain.jooq.tables.records.UserRoleRecord
import au.org.ala.sc.util.HTTP_NOT_FOUND
import au.org.ala.sc.util.isUuid
import au.org.ala.sc.util.logger
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.slf4j.Logger
import java.util.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

/**
 * We can't have a request scoped object that takes a User parameter
 * because the request scope is constructed before the filters are run, so
 * we create a request scoped factory that takes the user param and turns it
 * into a [CalendarService].
 *
 * A function that takes a [User] object and returns a [CalendarService]
 */
typealias CalendarServiceFactory = (user: User) -> CalendarService

/**
 * Explicit interface so that jersey's DI isn't confused.
 */
interface ICalendarServiceFactory : CalendarServiceFactory {
    override fun invoke(user: User): CalendarService
}

/**
 * Factory that applies the jOOQ [DSLContext], [ProfileServiceClient], [SeasonService] via [SeasonServiceFactory],
 * [defaultDataResourceUid] and [calendarTag] to the [DefaultCalendarService]
 */
class DefaultCalendarServiceFactory(
    private val config: Configuration,
    private val profileServiceClient: ProfileServiceClient,
    private val calendarUuidResolver: CalendarUuidResolver,
    private val userService: UserService,
    private val seasonServiceFactory: SeasonServiceFactory,
    private val defaultDataResourceUid: String,
    private val calendarTag: String) : ICalendarServiceFactory {

    override fun invoke(user: User) = build(user)

    fun build(user: User) : CalendarService {
        return DefaultTransactionalCalendarService(
            DefaultCalendarService(
                config,
                profileServiceClient,
                calendarUuidResolver,
                userService,
                seasonServiceFactory(user, config),
                defaultDataResourceUid,
                calendarTag,
                user
            )
        )
    }
}

/**
 * Interface for operations on seasonal calendars.
 */
interface CalendarService {
    /**
     * Get all seasonal calendars visible to the current context, if [publishedOnly] is set
     * then only calendars that are published are returned.
     */
    fun getSeasonalCalendars(publishedOnly: Boolean = true) : List<SeasonalCalendarDto>

    /**
     * Get a seasonal calendar by [name].  [name] may be either a UUID or the calendar's [shortName].  Both published
     * and unpublished calendars may be returned via this method, access control for unpublished calendars must be
     * applied externally.
     */
    fun getSeasonalCalendar(name: String) : SeasonalCalendarDto

    /**
     * Find a [SeasonalCalendarDto] by its [language] field.  Only published calendars may be returned via this method.
     */
    fun findSeasonalCalendarByLanguage(language: String) : SeasonalCalendarDto

    /**
     * Insert a [SeasonalCalendarDto] and return its newly assigned [UUID].
     */
    fun insertCalendar(calendar: SeasonalCalendarDto): UUID

    /**
     * Update the calendar with [calendarName] using the body of [calendar].
     */
    fun updateCalendar(calendarName: String, calendar: SeasonalCalendarDto)

    /**
     * Set the published flag to [publish] on the seasonal calendar with the name [calendarName].
     */
    fun publishSeasonalCalendar(calendarName: String, publish: Boolean = true)

    /**
     * Delete the seasonal calendar with name [calendarName].
     */
    fun deleteCalendar(calendarName: String)

    /**
     * Save the list of [permissions] for a given [calendarName]
     */
    fun saveCalendarPermissions(calendarName: String, permissions: List<CalendarUser>)
}

interface TransactionableCalendarService : CalendarService, Transactionable<TransactionableCalendarService>

/**
 * Applies transactional contexts around operations.
 *
 * Once a transaction context is started, it's configuration is propagated to the calendar service and
 * dependent services and all database calls are in the context of the transaction.
 */
class DefaultTransactionalCalendarService(
    override val service: TransactionableCalendarService
) : CalendarService by service, Transactional<TransactionableCalendarService> {

    override fun updateCalendar(calendarName: String, calendar: SeasonalCalendarDto) {
        return CalendarService::updateCalendar.bindLast(calendarName, calendar).runInTransaction()
//        return calendarService::updateCalendar.partially1(calendarName).partially1(calendar).runInTransaction()
//        configuration.dsl().transaction { txConfig ->
//            calendarService.withTransaction(txConfig).updateCalendar(calendarName, calendar)
//        }
    }

    override fun insertCalendar(calendar: SeasonalCalendarDto): UUID {
        return CalendarService::insertCalendar.partially2(calendar).runInTransaction()
//        return CalendarService::insertCalendar.partially2(calendar).runInTransaction()
//        return configuration.dsl().transactionResult { txConfig ->
//            calendarService.withTransaction(txConfig).insertCalendar(calendar)
//        }
    }

    override fun getSeasonalCalendars(publishedOnly: Boolean): List<SeasonalCalendarDto> {
        return CalendarService::getSeasonalCalendars.partially2(publishedOnly).runInTransaction(readOnly = true)
//        return configuration.forReadOnlyTransaction().dsl().transactionResult { txConfig ->
//            calendarService.withTransaction(txConfig).getSeasonalCalendars(publishedOnly)
//        }
    }

    override fun getSeasonalCalendar(name: String): SeasonalCalendarDto {
        return CalendarService::getSeasonalCalendar.partially2(name).runInTransaction(readOnly = true)
//        return configuration.forReadOnlyTransaction().dsl().transactionResult { txConfig ->
//            calendarService.withTransaction(txConfig).getSeasonalCalendar(name)
//        }
    }

    override fun saveCalendarPermissions(calendarName: String, permissions: List<CalendarUser>) =
        CalendarService::saveCalendarPermissions.bindLast(calendarName, permissions).runInTransaction()

//    private inline fun <reified T> (CalendarService.() -> T).runInTransaction(): T {
//        return configuration.runInTransaction(this)
////        return configuration.dsl().transactionResult { txConfig ->
////            return@transactionResult calendarService.withTransaction(txConfig).f()
////        }
//    }
//
//    private inline fun <reified T> (CalendarService.() -> T).runInReadOnlyTransaction(): T {
//        return configuration.forReadOnlyTransaction().runInTransaction(this)
////        return configuration.forReadOnlyTransaction().dsl().transactionResult { txConfig ->
////            return@transactionResult calendarService.withTransaction(txConfig).f()
////        }
//    }

//    private inline fun <reified T> transactionResult(crossinline f: CalendarService.() -> T): T {
//        return configuration.transactionResult(f)
////        return configuration.dsl().transactionResult { txConfig ->
////            return@transactionResult calendarService.withTransaction(txConfig).f()
////        }
//    }

//    private inline fun <reified T> readOnlyTransaction(crossinline f: CalendarService.() -> T): T {
//        return configuration.forReadOnlyTransaction().transactionResult(f)
////        return configuration.forReadOnlyTransaction().dsl().transactionResult { txConfig ->
////            return@transactionResult calendarService.withTransaction(txConfig).f()
////        }
//    }

//    private inline fun <reified T> Configuration.runInTransaction(crossinline f: CalendarService.() -> T): T {
//        return this.dsl().transactionResult { txConfig ->
//            return@transactionResult calendarService.withTransaction(txConfig).f()
//        }
//    }

    private fun <P1, P2, P3, R> ((P1, P2, P3) -> R).bindLast(p2: P2, p3: P3): (P1) -> R = { p1: P1 -> this(p1, p2, p3) }
}

data class DefaultCalendarService(
    override val configuration: Configuration,
    private val profileServiceClient: ProfileServiceClient,
    private val calendarUuidResolver: CalendarUuidResolver,
    private val userService: UserService,
    private val seasonService: SeasonService,
    private val defaultDataResourceUid: String,
    private val calendarTag: String,
    private val user: User) : TransactionableCalendarService {

    companion object {
        private val log = logger()
    }

    override fun withTransaction(configuration: Configuration): TransactionableCalendarService {
        return this.copy(
            configuration = configuration,
            seasonService = (seasonService as? Transactionable<out SeasonService>)?.withTransaction(configuration) ?: seasonService,
            userService = (userService as? Transactionable<out UserService>)?.withTransaction(configuration) ?: userService
        )
//        return DefaultCalendarService(
//            configuration,
//            profileServiceClient,
//            calendarUuidResolver,
//            userService,
//            (seasonService as? TransactionableSeasonService)?.withTransaction(configuration) ?: seasonService,
//            defaultDataResourceUid,
//            calendarTag,
//            user
//        )
    }

    private val calendarDao by lazy(NONE) { CalendarDao(configuration) }

    override fun updateCalendar(calendarName: String, calendar: SeasonalCalendarDto) {
        val opus = getOpus(calendarName)
        val opusId = opus.uuid
        if (calendar.collectionUuid != opusId) {
            log.error("Provided calendar name $calendarName, opus UUID ($opusId) doesn't match DTO UUID (${calendar.collectionUuid})")
            throw CalendarException("Provided calendar name $calendarName, opus UUID ($opusId) doesn't match DTO UUID (${calendar.collectionUuid})")
        }
        val opusUuid = UUID.fromString(opusId)
        opus.populateOpusFromSeasonalCalendar(calendar)
        val updateOpusResponse = profileServiceClient.updateOpus(opusId, user.name, opus).execute()
        if (updateOpusResponse.isSuccessful && updateOpusResponse.body()!!.isSuccess) {
            calendarDao.update(mapCalendarDtoToRecord(calendar))
            calendar.seasons.onEach { season ->
                seasonService.saveSeason(opusUuid, season)
            }
        } else {
            log.error("Couldn't update Opus for id {} with response {}: ", opusId, updateOpusResponse.code(), updateOpusResponse.errorBody()?.string())
            throw CalendarUpdateException(opusId)
        }
    }

    override fun insertCalendar(calendar: SeasonalCalendarDto): UUID {
        val tag = getTag() ?: throw CalendarException("Tag $calendarTag not found!")

        val createOpusResponse = profileServiceClient.createOpus(user.name, opusFromSeasonalCalendar(calendar, tag)).execute()

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
                saveCalendarPermissions(opusUuid, listOf(CalendarUser(user.name, true, false)))
                return opusUuid
            } catch (e: Exception) {
                log.error("Couldn't insert calendar record {}", record, e)
                if (!profileServiceClient.deleteOpus(opusId, user.name).execute().isSuccessful) {
                    log.error("Couldn't delete corresponding Opus!")
                }
                throw e
            }
        } else {
            log.error("Couldn't create opus with code {}: ", createOpusResponse.code(), createOpusResponse.errorBody()?.string())
            throw ProfileServiceException(createOpusResponse.code(), "Couldn't create collection")
        }
    }

    private fun getOpus(name: String): Opus {
        return au.org.ala.sc.services.getOpus(log, profileServiceClient, name, user.name)
    }

    override fun getSeasonalCalendar(name: String) : SeasonalCalendarDto {
        val opus = getOpus(name)
        val opusUuid = UUID.fromString(opus.uuid)

        val calendar = calendarDao.findById(opusUuid)
        return constructCalendarDto(opus, calendar).copy(seasons = seasonService.getSeasonsForCalendarId(opusUuid))
    }

    override fun findSeasonalCalendarByLanguage(language: String) : SeasonalCalendarDto {
        val calendarList = calendarDao.fetchByLanguage(language)
        if (calendarList.isEmpty()) throw CalendarNotFoundException(language)
        val calendar = calendarList.first()

        if (!calendar.published) throw CalendarNotFoundException(language)

        val opus = getOpus(calendar.collectionUuid.toString())
        val opusUuid = UUID.fromString(opus.uuid)
        return constructCalendarDto(opus, calendar).copy(seasons = seasonService.getSeasonsForCalendarId(opusUuid))
    }

    override fun getSeasonalCalendars(publishedOnly: Boolean) : List<SeasonalCalendarDto> {
        val getOperaResponse = profileServiceClient.getOperaByTag(calendarTag, user.name, emptyMap()).execute()
        if (getOperaResponse.isSuccessful) {
            val opera = getOperaResponse.body()!!
            val operaMap = opera.associateBy { UUID.fromString(it.uuid) }
            val calendars = calendarDao.fetchByCollectionUuid(*operaMap.keys.toTypedArray())
            val calendarsMap = calendars.associateBy { it.collectionUuid }
            val comboMap = operaMap.mapValues { it.value to calendarsMap[it.key] }

            val (filteredComboMap, authorised, users) = if (publishedOnly) {
                // for published Only calendars we don't care about authorised users, so we skip grabbing them  - perhaps this should be two methods...
                Triple(comboMap.filterValues { pair -> pair.second?.published ?: false }, emptyMap(), Optional.empty())
            } else {
                //
                val users = userService.authorisedUsers(operaMap.keys)
                val authorised = userService.authorisedCalendars(user, operaMap.keys)
                Triple(comboMap.filterKeys { uuid -> authorised.containsKey(uuid) }, authorised, Optional.of(users))
            }
            return filteredComboMap.map { (uuid, pair) ->
                constructCalendarDto(
                    pair.first,
                    pair.second,
                    authorised[uuid]?.let { role -> CALENDAR_ADMIN_ROLES.contains(role) } ,
                    users.map { it[uuid] ?: emptyList() }.orElse(null) // return an empty list for published only = false, null otherwise
                )
            }
        } else {
            log.error("getOperaByTag failed for tag {}, publishedOnly: {} with code {}, body: {}", calendarTag, publishedOnly, getOperaResponse.code(), getOperaResponse.errorBody()?.string())
            throw ProfileServiceException(getOperaResponse.code(), "Couldn't get collections (published only: $publishedOnly)")
        }
    }

//    private fun fetchCalendars(published: Boolean) {
//        if (!published) {
//            calendarDao.findAll()
//        } else {
//            configuration.dsl()
//                .selectFrom(CALENDAR)
//                .where(CALENDAR.PUBLISHED.eq(false))
//                .and(exists(
//                    select()
//                        .from(USER_ROLE)
//                        .where(USER_ROLE.CALENDAR_UUID.eq(CALENDAR.COLLECTION_UUID))
//                        .and(USER_ROLE.USER_ID.eq(user.name))
//                ))
//        }
//    }

    private fun getTag(): Tag? {
        val response = profileServiceClient.getTags(user.name).execute()
        return if (response.isSuccessful) {
            response.body()!!.tags.find { it.abbrev == calendarTag || it.name == calendarTag || it.uuid == calendarTag }
        } else {
            log.error("Couldn't get tags from profiles service because ${response.code()}: ${response.errorBody()?.string()}")
            throw ProfileServiceException(response.code(), "Couldn't get tags")
        }
    }

    private fun opusFromSeasonalCalendar(calendar: SeasonalCalendarDto, tag: Tag) : Opus {
        return Opus().apply {
            dataResourceUid = defaultDataResourceUid
            isAutoApproveShareRequests = false
            isAutoDraftProfiles = false
            isKeepImagesPrivate = true
            isPrivateCollection = true
            isUsePrivateRecordData = false
            tags.add(tag)
            populateOpusFromSeasonalCalendar(calendar)
        }
    }

    private fun Opus.populateOpusFromSeasonalCalendar(calendar: SeasonalCalendarDto): Opus {
        shortName = calendar.shortName
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
        brandingConfig.pdfLicense = calendar.limitations // TODO where is the license long description
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
            calendar.organisationName,
            calendar.contributors.toTypedArray(),
            calendar.contactName,
            calendar.keywords.toTypedArray(),
            calendar.development,
            calendar.developmentReason,
            calendar.references.toTypedArray(),
            calendar.referenceLinks.toTypedArray(),
            calendar.languageGroup,
            calendar.published,
            calendar.welcomeCountry,
            calendar.welcomeCountryMedia,
            calendar.whoWeAreDescription,
            calendar.ourCountry,
            calendar.ourHistory,
            calendar.logos.toTypedArray(),
            calendar.mediaLinks.toTypedArray(),
            calendar.externalLinks.toTypedArray()
        )
    }

    private fun constructCalendarDto(opus: Opus, calendar: Calendar?, isAdmin: Boolean? = null, users: List<CalendarUser>? = null) : SeasonalCalendarDto {
        return SeasonalCalendarDto(
            collectionUuid = opus.uuid,
            shortName = opus.shortName ?: "",
            name = opus.title,
            description = opus.description ?: "",
            imageUrl = opus.opusLayoutConfig.images?.firstOrNull()?.imageUrl ?: "",
            websiteUrl = calendar?.website ?: "",
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
            published = calendar?.published ?: false,
            languageGroup = calendar?.language ?: "",
            welcomeCountry = calendar?.welcomeCountry ?: "",
            welcomeCountryMedia = calendar?.welcomeCountryMedia ?: "",
            whoWeAreDescription = calendar?.whoWeAre ?: "",
            ourCountry = calendar?.ourCountry ?: "",
            ourHistory = calendar?.ourHistory ?: "",
            logos = calendar?.lgLogos?.toList() ?: mutableListOf(),
            mediaLinks = calendar?.mediaLinks?.toList() ?: mutableListOf(),
            externalLinks = calendar?.externalLinks?.toList() ?: mutableListOf(),
            admin = isAdmin,
            users = users
        )
    }

    override fun publishSeasonalCalendar(calendarName: String, publish: Boolean) {
        val uuid = calendarUuidResolver.getCalendarUuid(calendarName, user.name)
        val updates = configuration.dsl().update(CALENDAR).set(CALENDAR.PUBLISHED, publish).where(CALENDAR.COLLECTION_UUID.eq(uuid)).execute()
        if (updates == 0) {
            log.error("Attempted to publish Calendar $calendarName but 0 updates occured")
            throw CalendarNotFoundException(calendarName)
        } else if (updates > 1) {
            log.error("Too many updates ($updates) attempting to publish Calendar $calendarName")
        }
    }

    override fun saveCalendarPermissions(calendarName: String, permissions: List<CalendarUser>) {
        val uuid = calendarUuidResolver.getCalendarUuid(calendarName, user.name)
        return saveCalendarPermissions(uuid, permissions)
    }
    private fun saveCalendarPermissions(uuid: UUID, permissions: List<CalendarUser>) {
        val (adminId, editorId) = RoleDao(configuration).run {
            listOf(fetchOne(ROLE.NAME, ROLE_CALENDAR_ADMIN), fetchOne(ROLE.NAME, ROLE_CALENDAR_EDITOR)).map { it.id }
        }

        val records = permissions
            .map { UserRoleRecord(it.username, if (it.admin) adminId else editorId, uuid )}


        val dsl = configuration.dsl()
        val insert = dsl.insertInto(USER_ROLE)

        if (records.isNotEmpty()) {
            val inserts = records.tail().fold(insert.set(records.first())) { acc, record ->
                acc.newRecord().set(record)
            }.onConflictDoNothing().execute()
            // on conflict do nothing because the whole row is the PK, therefore there is
            // nothing to upsert if there is a conflict.
            log.debug("Inserted {} rows from {} permissions", inserts, records.size)
        }

        val deletes = dsl.
            deleteFrom(USER_ROLE)
            .where(
                USER_ROLE.CALENDAR_UUID.eq(uuid)
            ).and(
                row(USER_ROLE.ROLE_ID, USER_ROLE.USER_ID)
//                UserRoleRecord().fieldsRow()
                    .notIn(
                        records.map { row(it.roleId, it.userId) }
                    )
            ).execute()
        log.debug("Deleted {} rows from user_role", deletes)
    }

    override fun deleteCalendar(calendarName: String) {
        val uuid = calendarUuidResolver.getCalendarUuid(calendarName, user.name)

        val deletes= configuration.dsl().delete(CALENDAR).where(CALENDAR.COLLECTION_UUID.eq(uuid)).execute()

        if (deletes == 0) {
            log.info("No local db entry for calendar $calendarName")
        }

        val deleteResponse = profileServiceClient.deleteOpus(calendarName, user.name).execute()
        if (!deleteResponse.isSuccessful) {
            val code = deleteResponse.code()
            if (code == HTTP_NOT_FOUND) {
                throw CalendarNotFoundException(calendarName)
            } else {
                log.error("Error deleting opus with name $calendarName, error text: ${deleteResponse.errorBody()?.string()}")
                throw CalendarDeleteException(calendarName)
            }
        }
    }
}

open class CalendarException(message: String) : RuntimeException(message) {
    override fun fillInStackTrace() = this
}
open class NamedCalendarException(val name: String, message: String) : CalendarException(message)
open class ProfileServiceException(val code: Int, message: String) : CalendarException(message)
class CalendarNotFoundException(name: String) : NamedCalendarException(name, "Couldn't find a calendar for $name")
class OpusRetrieveException(code: Int, val name: String) : ProfileServiceException(code, "Couldn't get collection for $name")
class CalendarUpdateException(name: String) : NamedCalendarException(name, "Couldn't update calendar $name")
class CalendarDeleteException(name: String) : NamedCalendarException(name, "Couldn't delete calendar $name")

interface CalendarUuidResolver {
    fun getCalendarUuid(calendarName: String, username: String): UUID
}

class DefaultCalendarUuidResolver @Inject constructor(
    private val profileServiceClient: ProfileServiceClient
): CalendarUuidResolver {

    companion object {
        val log = logger()
    }
    override fun getCalendarUuid(calendarName: String, username: String): UUID {
        return UUID.fromString(if (calendarName.isUuid()) {
            calendarName
        } else {
            getOpus(log, profileServiceClient, calendarName, username).uuid
        })
    }
}

/**
 * Extract the logic to get the [Opus] from the [ProfileServiceClient] so that it
 * can be reused between the [CalendarUuidResolver] and the [CalendarService].
 */
private fun getOpus(
    log: Logger,
    profileServiceClient: ProfileServiceClient,
    name: String,
    username: String
): Opus {
    val getOpusResponse = profileServiceClient.getOpus(name, username).execute()
    if (getOpusResponse.isSuccessful) {
        return getOpusResponse.body()!!
    } else {
        if (getOpusResponse.code() == HTTP_NOT_FOUND) {
            log.error("Couldn't find opus with name $name")
            throw CalendarNotFoundException(name)
        } else {
            log.error("Couldn't find opus with name $name and error body ${getOpusResponse.errorBody()?.string()}")
            throw OpusRetrieveException(getOpusResponse.code(), "Error getting collection")
        }
    }
}