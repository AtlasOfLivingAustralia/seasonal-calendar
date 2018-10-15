package au.org.ala.sc.services

import au.org.ala.sc.api.CalendarUser
import au.org.ala.sc.auth.*
import au.org.ala.sc.domain.jooq.Tables.*
import org.jooq.Configuration
import org.jooq.impl.DSL
import org.jooq.impl.DSL.*
import java.beans.ConstructorProperties
import java.util.*

interface UserService {
    fun isUserAuthorisedForCalendar(roles: Array<String>, username: String, calendarId: String, readOnly: Boolean = false): Boolean

    fun authorisedCalendars(user: User, calendarIds: Collection<UUID>): Map<UUID, String>

    fun authorisedCalendars(user: User): Map<String, List<UUID>>

    fun authorisedUsers(calendarIds: Collection<UUID>): Map<UUID, List<CalendarUser>>

    fun hasRoleForAnyCalendar(user: User, role: String): Boolean
}

interface TransactionableUserService: UserService, Transactionable<TransactionableUserService>

data class DefaultUserService(
    override val configuration: Configuration,
    private val calendarUuidResolver: CalendarUuidResolver
): TransactionableUserService {

    override fun isUserAuthorisedForCalendar(roles: Array<String>, username: String, calendarId: String, readOnly: Boolean): Boolean {
        val uuid = calendarUuidResolver.getCalendarUuid(calendarId, username)
        val dsl = configuration.dsl()

        // All users are allowed to read published calendars
        if (readOnly) {
            val published = dsl.select(CALENDAR.PUBLISHED).from(CALENDAR).where(CALENDAR.COLLECTION_UUID.eq(uuid)).fetchOne(CALENDAR.PUBLISHED)
            if (published) return true
        }
        val select = DSL
            .selectOne()
            .from(USER_ROLE).join(ROLE).onKey()
            .where(USER_ROLE.CALENDAR_UUID.eq(uuid)).and(ROLE.NAME.`in`(*roles)).and(USER_ROLE.USER_ID.eq(username))

        return dsl.fetchExists(select)
    }

    override fun authorisedCalendars(user: User) = when (user) {
        is User.Authenticated -> {
            val results = configuration.dsl()
                .select(ROLE.NAME, arrayAgg(USER_ROLE.CALENDAR_UUID))
                .from(USER_ROLE).join(ROLE).onKey()
                .where(USER_ROLE.USER_ID.eq(user.name))
                .groupBy(ROLE.NAME)
                .fetch()

            results.associate { (role, uuids) -> role to uuids.toList() }
        }
        is User.Anonymous -> emptyMap()
    }

    override fun authorisedCalendars(user: User, calendarIds: Collection<UUID>): Map<UUID, String> = when (user) {
        is User.Authenticated -> {
            if (user.hasRole(ROLE_ADMIN) || user.hasRole(ROLE_SC_ADMIN)) {
                calendarIds.associate { it to ROLE_SC_ADMIN }
            } else {
                val results = configuration.dsl()
                    .select(USER_ROLE.CALENDAR_UUID, ROLE.NAME)
                    .from(USER_ROLE).join(ROLE).onKey()
                    .where(USER_ROLE.CALENDAR_UUID.`in`(calendarIds)).and(USER_ROLE.USER_ID.eq(user.name))
                    .fetchInto(AuthorisedCalendarResult::class.java)

                results.associate { it.collectionUuid to it.name }
            }
        }
        else -> emptyMap()
    }

    override fun authorisedUsers(calendarIds: Collection<UUID>): Map<UUID, List<CalendarUser>> {
        return configuration.dsl()
            .select(
                USER_ROLE.CALENDAR_UUID,
                USER_ROLE.USER_ID.`as`("username"),
                boolOr(ROLE.NAME.eq(ROLE_CALENDAR_ADMIN)).`as`("admin"),
                boolOr(ROLE.NAME.eq(ROLE_CALENDAR_EDITOR)).`as`("editor")
            )
            .from(USER_ROLE).join(ROLE).onKey()
            .where(USER_ROLE.CALENDAR_UUID.`in`(calendarIds))
            .groupBy(USER_ROLE.CALENDAR_UUID, USER_ROLE.USER_ID)
            .fetchGroups(USER_ROLE.CALENDAR_UUID, CalendarUser::class.java)
    }

    override fun hasRoleForAnyCalendar(user: User, role: String): Boolean {
        if (!PER_CALENDAR_ROLES.contains(role)) return false
        return configuration.dsl().fetchExists(selectOne().from(USER_ROLE).join(ROLE).onKey().where(USER_ROLE.USER_ID.eq(user.name)).and(ROLE.NAME.eq(role)))
    }

    override fun withTransaction(configuration: Configuration) = copy(configuration = configuration)

}

data class AuthorisedCalendarsResult @ConstructorProperties("collectionUuid", "name") constructor(
    val collectionUuid: UUID,
    val userId: String,
    val name: String
)

data class AuthorisedCalendarResult @ConstructorProperties("collectionUuid", "name") constructor(
    val collectionUuid: UUID,
    val name: String
)