package au.org.ala.calendar

import au.org.ala.web.AuthService
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(UserService)
class UserServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test userIsAlaAdmin - security.cas.alaAdminRole configuration"() {

        Boolean isAdmin

        given:
        AuthService authService = Mock(AuthService)
        service.authService = authService
        authService.userInRole(_) >> true

        when: "No Configuration for security.cas.alaAdminRole"
        grailsApplication.config.security.cas.alaAdminRole = null
        isAdmin = service.userIsAlaAdmin()

        then: "No one is regarded as admin"
        assert !isAdmin

        when: "One role given for security.cas.alaAdminRole"
        grailsApplication.config.security.cas.alaAdminRole = "AnyRoleName"
        isAdmin = service.userIsAlaAdmin()

        then: "Our user is an admin"
        assert isAdmin


    }

    void "test userIsAlaAdmin - security.cas.alaAdminRole configuration multiple roles"() {
        Boolean isAdmin

        given:
        AuthService authService = Mock(AuthService)
        service.authService = authService

        authService.userInRole("OneRole") >> false
        authService.userInRole("TwoRoles") >> false
        authService.userInRole("ThreeRoles") >> true

        when: "More than role given for security.cas.alaAdminRole"
        grailsApplication.config.security.cas.alaAdminRole = "OneRole,TwoRoles,ThreeRoles"
        isAdmin = service.userIsAlaAdmin()

        then: "Our user is an admin"
        assert isAdmin
    }

    void "test userIsAlaAdmin - user not in roles"() {

        Boolean isAdmin

        given:
        AuthService authService = Mock(AuthService)
        service.authService = authService
        authService.userInRole(_) >> false

        when: "User is not part of the admin roles"
        grailsApplication.config.security.cas.alaAdminRole = "OneRole,TwoRoles,ThreeRoles"
        isAdmin = service.userIsAlaAdmin()

        then: "Our user is NOT an admin"
        assert !isAdmin

    }
}