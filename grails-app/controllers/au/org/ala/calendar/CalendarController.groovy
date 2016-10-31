package au.org.ala.calendar

import au.org.ala.web.AlaSecured
import grails.converters.JSON

class CalendarController {


    CalendarService calendarService

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
        def result;
        try{

            Calendar calendar = calendarService.get(id)

            if(calendar){
                result = [status:"ok", calendar: calendar]
            } else{
                result = [status:"error", error: "Invalid id"]
            }
        }
        catch(Exception ex){
            log.error("Error loading calendars.", ex);
            result = [status:"error", error: "Error loading calendar, please try again later."]
        }

        render result as JSON
    }


    @AlaSecured(value = ['ROLE_SC','ROLE_ADMIN'], anyRole = true)
    def settings() {
        return [id: params.id]
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN'], anyRole = true)
    def addCalendar() {
        def props = request.JSON
        Map result
        try{
            String id = UUID.randomUUID().toString()
            props.calendarId = id
            calendarService.create(props);

            result = [status:'ok', calendarId: id]
        } catch(Exception exception) {
            result = [status: 'error', error: "Error saving the calendar, please try again later."]
        }

        render result as JSON;
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN'], anyRole = true)
    def editCalendar(String id) {
        def props = request.JSON
        Map result
        try{
            props.calendarId = id
            result = calendarService.update(props)
        } catch(Exception e) {
            log.error("Error updating calendar", e)
            result = [status: 'error', error: "Error saving the calendar, please try again later."]
        }

        render result as JSON;
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN'], anyRole = true)
    def delete(String id) {
        render calendarService.delete(id) as JSON
    }
}
