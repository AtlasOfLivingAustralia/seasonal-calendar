package au.org.ala.sc.resources

import au.org.ala.sc.auth.User
import au.org.ala.sc.services.UserService
import io.dropwizard.auth.Auth
import java.util.*
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("roles")
class RolesResource @Inject constructor(
    private val userService: UserService
) {

    @GET
    fun roles(@Auth user: User): Map<String, List<UUID>> {
        return userService.authorisedCalendars(user)
    }
}