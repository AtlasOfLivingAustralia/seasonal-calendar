package au.org.ala.sc.resources

import au.org.ala.sc.api.CalendarSavedDto
import au.org.ala.sc.api.CalendarUser
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.auth.*
import au.org.ala.sc.services.ICalendarServiceFactory
import au.org.ala.sc.util.logger
import io.dropwizard.auth.Auth
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
class CalendarResource @Inject constructor(
    private val calendarServiceFactory: ICalendarServiceFactory
) {

    @GET
    fun getCalendars(@QueryParam("publishedOnly") @DefaultValue("true") publishedOnly: Boolean, @Auth user: User) =
        calendarServiceFactory(user).getSeasonalCalendars(publishedOnly)

    @GET
    @Path("{calendarName}")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN, ROLE_CALENDAR_EDITOR], idParam = "calendarName", readOnly = true)
    fun getCalendar(@PathParam("calendarName") calendarName: String, @Auth user: User) =
        calendarServiceFactory(user).getSeasonalCalendar(calendarName)

    @PUT
    @Path("{calendarName}/publish")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN], idParam = "calendarName")
    fun publish(@PathParam("calendarName") calendarName: String, @Auth user: User) =
        calendarServiceFactory(user).publishSeasonalCalendar(calendarName)

    @PUT
    @Path("{calendarName}/unpublish")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN], idParam = "calendarName")
    fun unpublish(@PathParam("calendarName") calendarName: String, @Auth user: User) =
        calendarServiceFactory(user).publishSeasonalCalendar(calendarName, false)

    @POST
    @Path("{calendarName}/permissions")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN], idParam = "calendarName")
    fun permissions(@PathParam("calendarName") calendarName: String, permissions: List<CalendarUser>, @Auth user: User) {
        calendarServiceFactory(user).saveCalendarPermissions(calendarName, permissions)
    }

    @POST
    @RolesAllowed(ROLE_ADMIN, ROLE_SC_ADMIN, ROLE_SC_CREATOR)
    fun insertCalendar(@NotNull @Valid seasonalCalendarDto: SeasonalCalendarDto, @Auth user: User) : CalendarSavedDto {
        val uuid = calendarServiceFactory(user).insertCalendar(seasonalCalendarDto)
        return CalendarSavedDto(uuid.toString())
    }

    @POST
    @Path("{calendarName}")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN, ROLE_CALENDAR_EDITOR], idParam = "calendarName")
    fun updateCalendar(@PathParam("calendarName") calendarName: String, @NotNull @Valid seasonalCalendarDto: SeasonalCalendarDto, @Auth user: User): Unit =
        calendarServiceFactory(user).updateCalendar(calendarName, seasonalCalendarDto)

    @DELETE
    @Path("{calendarName}")
    @CalendarRolesAllowed(roles = [ROLE_CALENDAR_ADMIN], idParam = "calendarName")
    fun deleteCalendar(@PathParam("calendarName") calendarName: String, @Auth user: User) {
        calendarServiceFactory(user).deleteCalendar(calendarName)
    }
}