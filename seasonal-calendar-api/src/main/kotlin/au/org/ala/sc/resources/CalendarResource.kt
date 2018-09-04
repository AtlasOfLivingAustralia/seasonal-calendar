package au.org.ala.sc.resources

import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.services.CalendarService
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CalendarResource(
    val calendarService: CalendarService
) {

    @GET
    fun getCalendars() =
        try {
            calendarService.getSeasonalCalendars()
        } catch (e: Exception) {
            throw WebApplicationException("Unable to get calendars", e, 500)
        }

    @GET
    @Path("{calendarName}")
    fun getCalendar(@PathParam("calendarName") calendarName: String) =
        try {
            calendarService.getSeasonalCalendar(calendarName)
        } catch (e: Exception) {
            throw WebApplicationException("Unable to get calendar for name $calendarName", e, 500)
        }

    @POST
    fun insertCalendar(seasonalCalendarDto: SeasonalCalendarDto) : Response {
        calendarService.saveCalendar(seasonalCalendarDto)
        return Response.noContent().build()
    }

    @POST
    @Path("{calendarName}")
    fun updateCalendar(@PathParam("calendarName") calendarName: String, seasonalCalendarDto: SeasonalCalendarDto) : Response {
        calendarService.saveCalendar(seasonalCalendarDto)
        return Response.noContent().build()
    }

    @DELETE
    @Path("{calendarName}")
    fun deleteCalendar(@PathParam("calendarName") calendarName: String) {
        // TODO
//        calendarService.deleteCalendar(calendarName)
    }
}