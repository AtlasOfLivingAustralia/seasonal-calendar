package au.org.ala.sc.resources

import au.org.ala.sc.api.CalendarSavedDto
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.services.CalendarService
import au.org.ala.sc.util.logger
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CalendarResource(
    private val calendarService: CalendarService
) {

    companion object {
        val log = logger()
    }

    @GET
    fun getCalendars(@QueryParam("publishedOnly") @DefaultValue("true") publishedOnly: Boolean) = calendarService.getSeasonalCalendars(publishedOnly)

    @GET
    @Path("{calendarName}")
    fun getCalendar(@PathParam("calendarName") calendarName: String) = calendarService.getSeasonalCalendar(calendarName)

    @PUT
    @Path("{calendarName}/publish")
    fun publish(@PathParam("calendarName") calendarName: String) = calendarService.publishSeasonalCalendar(calendarName)

    @PUT
    @Path("{calendarName}/unpublish")
    fun unpublish(@PathParam("calendarName") calendarName: String) = calendarService.publishSeasonalCalendar(calendarName, false)

    @POST
    fun insertCalendar(@NotNull @Valid seasonalCalendarDto: SeasonalCalendarDto) : CalendarSavedDto {
        val uuid = calendarService.insertCalendar(seasonalCalendarDto)
        return CalendarSavedDto(uuid.toString())
    }

    @POST
    @Path("{calendarName}")
    fun updateCalendar(@PathParam("calendarName") calendarName: String, @NotNull @Valid seasonalCalendarDto: SeasonalCalendarDto) = calendarService.updateCalendar(calendarName, seasonalCalendarDto)

    @DELETE
    @Path("{calendarName}")
    fun deleteCalendar(@PathParam("calendarName") calendarName: String) {
            calendarService.deleteCalendar(calendarName)
    }
}