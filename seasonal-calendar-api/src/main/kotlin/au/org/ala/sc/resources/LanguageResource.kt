package au.org.ala.sc.resources

import au.org.ala.sc.services.CalendarService
import au.org.ala.sc.util.logger
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("language")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class LanguageResource(
        private val calendarService: CalendarService
) {

    companion object {
        val log = logger()
    }

    @GET
    @Path("{language}")
    fun findCalendar(@PathParam("language") language: String) = calendarService.findSeasonalCalendar(language)
}