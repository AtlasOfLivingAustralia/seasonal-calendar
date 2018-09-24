package au.org.ala.sc.resources

import au.org.ala.sc.api.CalendarSavedDto
import au.org.ala.sc.api.SeasonalCalendarDto
import au.org.ala.sc.services.CalendarException
import au.org.ala.sc.services.CalendarNotFoundException
import au.org.ala.sc.services.CalendarService
import org.slf4j.LoggerFactory
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CalendarResource(
    val calendarService: CalendarService
) {

    companion object {
        val log = LoggerFactory.getLogger(CalendarResource::class.java)
    }

    @GET
    fun getCalendars(@QueryParam("publishedOnly") @DefaultValue("true") publishedOnly: Boolean) =
        try {
            calendarService.getSeasonalCalendars(publishedOnly)
        } catch (e: Exception) {
            throw WebApplicationException("Unable to get calendars", e, 500)
        }

    @GET
    @Path("{calendarName}")
    fun getCalendar(@PathParam("calendarName") calendarName: String) =
        translateCalendarException("Error retrieving calendar for name $calendarName") {
            calendarService.getSeasonalCalendar(calendarName)
        }

    @PUT
    @Path("{calendarName}/publish")
    fun publish(@PathParam("calendarName") calendarName: String) =
        translateCalendarException("Error publishing calendar $calendarName") {
            calendarService.publishSeasonalCalendar(calendarName)
            Response.noContent().build()
        }

    @PUT
    @Path("{calendarName}/unpublish")
    fun unpublish(@PathParam("calendarName") calendarName: String) =
        translateCalendarException("Error unpublishing calendar $calendarName") {
            calendarService.publishSeasonalCalendar(calendarName, false)
            Response.noContent().build()
        }

    @POST
    fun insertCalendar(seasonalCalendarDto: SeasonalCalendarDto) : CalendarSavedDto {
        val uuid = calendarService.insertCalendar(seasonalCalendarDto)
        return CalendarSavedDto(uuid.toString())
    }

    @POST
    @Path("{calendarName}")
    fun updateCalendar(@PathParam("calendarName") calendarName: String, seasonalCalendarDto: SeasonalCalendarDto) =
        translateCalendarException("Couldn't update calendar $calendarName") {
            calendarService.saveCalendar(seasonalCalendarDto)
            Response.noContent().build()
        }

    @DELETE
    @Path("{calendarName}")
    fun deleteCalendar(@PathParam("calendarName") calendarName: String) {
        translateCalendarException("Couldn't delete calendar $calendarName") {
            calendarService.deleteCalendar(calendarName)
        }
    }

    private fun <T> translateCalendarException(errorMessage: String, f: () -> T): T? = try {
            f()
        } catch (e: CalendarNotFoundException) {
            throw WebApplicationException("Calendar not found", e, 404)
        } catch (e: CalendarException) {
            throw WebApplicationException(errorMessage, e, 500)
        } catch (e: Exception) {
            // TODO should we log this exception because we're not expecting it?
            log.error(errorMessage, e)
            throw WebApplicationException(errorMessage, e, 500)
        }

}