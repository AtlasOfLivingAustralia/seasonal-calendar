package au.org.ala.sc.auth

import au.org.ala.sc.services.UserService
import au.org.ala.web.UserDetails
import io.dropwizard.auth.Authorizer
import java.security.Principal
import java.util.*

const val ROLE_ADMIN = "ROLE_ADMIN"
const val ROLE_SC_ADMIN = "ROLE_SC_ADMIN"
const val ROLE_SC_CREATOR = "ROLE_SC_CREATOR"
const val ROLE_CALENDAR_ADMIN = "ROLE_CALENDAR_ADMIN"
const val ROLE_CALENDAR_EDITOR = "ROLE_CALENDAR_EDITOR"
const val ROLE_ANONYMOUS = "ROLE_ANONYMOUS"
val ADMIN_ROLES = setOf(ROLE_ADMIN, ROLE_SC_ADMIN)
val CALENDAR_ADMIN_ROLES = ADMIN_ROLES + ROLE_CALENDAR_ADMIN
val PER_CALENDAR_ROLES = setOf(ROLE_CALENDAR_ADMIN, ROLE_CALENDAR_EDITOR)

sealed class User : Principal {

    open fun hasRole(role: String): Boolean = ROLE_ANONYMOUS == role

    object Anonymous : User() {

        val asOptional: Optional<User> = Optional.of(this)
        override fun getName() = "anonymous"
    }
    class Authenticated(private val _name: String, val userinfo: UserInfo, val details: UserDetails) : User() {

        override fun hasRole(role: String): Boolean {
            return super.hasRole(role) || userinfo.role.contains(role) || details.roles.contains(role)
        }

        override fun getName(): String {
            return _name
        }
    }

}

class UserAuthorizer(private val userService: UserService) : Authorizer<User> {
    override fun authorize(principal: User, role: String) = when {
        role == ROLE_ANONYMOUS -> true
        principal is User.Authenticated -> principal.hasRole(role) || userService.hasRoleForAnyCalendar(principal, role)
        else -> false
    }
}