package au.org.ala.calendar

import au.org.ala.web.AlaSecured
import grails.converters.JSON

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CalendarController {


    CalendarService calendarService
    PermissionService permissionService
    UserService userService

    def listCalendars() {
        def calendars;

        try{
            List files = calendarService.list()
            calendars = files
        }
        catch(Exception ex){
            log.error("Error loading calendars.", ex);
            throw ex
        }

        render ([status:'ok', calendars:calendars] as JSON)
    }

    def detail() {
        return [id: params.id]
    }

    def getCalendar(String id) {
        try{

            Calendar calendar = calendarService.get(id)

            if(calendar){
                Map result = [calendar: calendar]
                render result as JSON
            } else{
                render status:HttpServletResponse.SC_NOT_FOUND, text: "Invalid id"
            }
        }
        catch(Exception ex){
            log.error("Error loading calendars.", ex);
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error loading calendar, please try again later."
        }
    }


    @AlaSecured(value = ['ROLE_SC','ROLE_ADMIN', 'ROLE_SC_ADMIN'], anyRole = true)
    def settings() {
        return [id: params.id]
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN', 'ROLE_SC_ADMIN'], anyRole = true)
    def addCalendar() {
        def props = request.JSON
        try{
            def userId = userService.getUser()?.userId
            if (!userId) { // Only authenticated users can create calendars
                render status: 401, text: 'You do not have permission to create a calendar'
                return
            }

            render calendarService.create(props) as JSON
        } catch(Exception exception) {
            log.error("Error saving calendar", exception )
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error saving the calendar, please try again later."
        }
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN', 'ROLE_SC_ADMIN'], anyRole = true)
    def editCalendar(String id) {
        def props = request.JSON

        try{

            if(!canCurrentUserEditCalendar(id)) {
                render status: 401, text: 'You do not have permission to edit this calendar'
                return
            }

            render calendarService.update(id, props) as JSON;

        } catch(Exception e) {
            log.error("Error updating calendar", e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error saving the calendar, please try again later."
        }
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN', 'ROLE_SC_ADMIN'], anyRole = true)
    def delete(String id) {
        try {
            if (!canCurrentUserEditCalendar(id)) {
                render status: HttpServletResponse.SC_UNAUTHORIZED, text: 'You do not have permission to delete this calendar'
                return
            }

            calendarService.delete(id)
            Map result = [calendarId: id]
            render result as JSON;

        } catch (Exception e) {
            log.error("Error updating calendar", e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error saving the calendar, please try again later."
        }

    }

    private Boolean canCurrentUserEditCalendar(String calendarId) {

        if(userService.userIsAlaAdmin()) {
            return true
        } else {
            return permissionService.isUserAdminForCalendar(userService.getUser()?.userId, calendarId)
        }
    }
}
