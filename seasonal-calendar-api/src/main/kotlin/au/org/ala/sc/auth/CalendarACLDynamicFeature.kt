package au.org.ala.sc.auth

import au.org.ala.sc.services.UserService
import org.glassfish.jersey.server.internal.LocalizationMessages
import org.glassfish.jersey.server.model.AnnotatedMethod
import java.lang.IllegalStateException
import javax.annotation.Priority
import javax.inject.Inject
import javax.ws.rs.ForbiddenException
import javax.ws.rs.Priorities
import javax.ws.rs.container.*
import javax.ws.rs.core.FeatureContext

class CalendarACLDynamicFeature @Inject constructor(private val userService: UserService) : DynamicFeature {
    override fun configure(resourceInfo: ResourceInfo, context: FeatureContext) {
        val am = AnnotatedMethod(resourceInfo.resourceMethod)
        val c = resourceInfo.resourceClass
        val cra = am.getAnnotation(CalendarRolesAllowed::class.java) ?: c.getAnnotation(CalendarRolesAllowed::class.java)
        if (cra != null) {
            val roles = cra.roles
            if (roles.isEmpty()) throw IllegalStateException("CalendarRolesAllowed.roles must not be empty")
            context.register(CalendarACLFilter(cra.roles, cra.idParam, cra.readOnly, userService))
        }
    }
}

@Priority(Priorities.AUTHORIZATION)
class CalendarACLFilter(
    private val roles: Array<String>,
    private val idParam: String,
    private val readOnly: Boolean,
    private val userService: UserService
) : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.securityContext.run{ isUserInRole(ROLE_ADMIN) || isUserInRole(ROLE_SC_ADMIN) }) return

        val ids = requestContext.uriInfo.pathParameters[idParam]
        if (ids == null || ids.isEmpty()) throw IllegalStateException("Expecting a paramater named $idParam but none was found")
        val id = ids.first()

        val username = requestContext.securityContext?.userPrincipal?.name ?: throw ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED())

        if (!userService.isUserAuthorisedForCalendar(roles, username, id, readOnly)) throw ForbiddenException(LocalizationMessages.USER_NOT_AUTHORIZED())
    }

}

/**
 * Checks the current user has a role for a given Calendar before allowing access
 */
annotation class CalendarRolesAllowed(
    /**
     * The Calendar roles allowed access, any over all admin roles are implicitly allowed access
     */
    val roles: Array<String> = [],
    /**
     * The request parameter that contains the calendar name or id.
     */
    val idParam: String = "",
    /**
     * Whether this is read only access.  Read only access is allowed for all published calendars.
     */
    val readOnly: Boolean = false
)