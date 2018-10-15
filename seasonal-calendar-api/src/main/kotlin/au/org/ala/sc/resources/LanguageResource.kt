package au.org.ala.sc.resources

import au.org.ala.sc.auth.User
import au.org.ala.sc.services.ICalendarServiceFactory
import io.dropwizard.auth.Auth
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("language")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
class LanguageResource @Inject constructor(
        private val calendarServiceFactory: ICalendarServiceFactory
) {

    @GET
    @Path("{language}")
    fun findCalendar(@PathParam("language") language: String, @Auth user: User) =
        calendarServiceFactory(user).findSeasonalCalendarByLanguage(language)
}