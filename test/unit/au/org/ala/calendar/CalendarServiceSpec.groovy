package au.org.ala.calendar

import au.org.ala.web.UserDetails
import com.github.fakemongo.Fongo
import com.mongodb.util.JSON
import grails.test.mixin.TestMixin
import grails.test.mixin.mongodb.MongoDbTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestMixin(MongoDbTestMixin)
class CalendarServiceSpec extends Specification {

    CalendarService service = new CalendarService()
    UserDetails userDetails = new UserDetails(displayName: "displayName", userName: "userName", userId: "userId")

    def setup() {
        Fongo fongo = new Fongo("seasonal-calendar-test")
        mongoDomain(fongo.mongo, [Calendar, UserPermission])

        defineBeans {
            commonService(CommonService)
            permissionService(PermissionService)

        }

        UserService userService = Mock(UserService)
        userService.getUser() >> userDetails
        service.userService = userService

        service.permissionService = grailsApplication.mainContext.permissionService
        grailsApplication.mainContext.commonService.grailsApplication = grailsApplication
        service.commonService = grailsApplication.mainContext.commonService


    }

    def cleanup() {
    }

    void "test CRUD operations for calendars"() {
        given:
        def calendarJsonString = '''
    {
        "calendarId" : "calendarId",
        "calendarName" : "Calendar Name",
        "description" : "Calendar description",
        "externalLink" : "https://www.google.com.au/",
        "imageUrl" : "http://www.fortresslockandsecurity.com/wp-content/uploads/2014/04/Austin-Locksmith.png",
        "seasons" : [
            {
                "features" : [

                ],
                "description" : "A new season",
                "months" : "Jan to Dec",
                "seasonName" : "Whatever",
                "weatherIcon" : "rainbow"
            }
        ],
        "multimedia" : "Multimedia",
        "calendarStatus" : "unpublished"
    }
'''


        when: "A calendar is created"
        def calendarJSON = JSON.parse(calendarJsonString)
        Calendar calendar = service.create(calendarJSON)

        then: "Calendar is created"
        assert calendar.calendarId == calendarJSON.calendarId
        assert calendar.calendarName == calendarJSON.calendarName
        assert calendar.description == calendarJSON.description
        assert calendar.externalLink == calendarJSON.externalLink
        assert calendar.imageUrl == calendarJSON.imageUrl
        assert calendar.multimedia == calendarJSON.multimedia
        assert calendar.calendarStatus == calendarJSON.calendarStatus


        then: "A user permission entry is created"
        def permissions = UserPermission.list()
        UserPermission userPermission = permissions[0]
        assert userPermission.entityId == calendar.calendarId
        assert userPermission.userId == "userId"


        then: "Calendar list should have one entry"
        def list = service.list()

        assert list.size() == 1

        then: "The only calendar should contain seasons"
        def newCalendar = list[0]
        assert newCalendar.seasons.size() > 0

        when: "A calendar is updated"
        calendarJSON.calendarName = "different"

        service.update(calendarJSON)

        then: "A new calendar name should be returned"
        Calendar calendarUpdated = service.get(calendarJSON.calendarId)
        assert calendarUpdated.calendarName == calendarJSON.calendarName

        when: "A calendar is deleted"
        service.delete(calendarJSON.calendarId)

        then: "The list of calendars should be empty"
        def emptylist = service.list()
        assert emptylist.size() == 0

    }
}
