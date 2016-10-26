package au.org.ala.calendar

import au.org.ala.web.AlaSecured
import grails.converters.JSON

class CalendarController {
    static String STATUS_DELETED = "deleted";

    def listCalendars() {
        def calendars = [];

        try{

            List files = FileSystem.getFiles("${grailsApplication.config.models.path}")
            files?.each {
                Map props = [:]
                Map calendar = FileSystem.load(it.path)
                if(calendar.calendarStatus != STATUS_DELETED ) {
                    props.calendarId = calendar?.calendarId;
                    props.name = calendar?.calendarName;
                    props.calendarStatus = calendar?.calendarStatus;
                    props.imageUrl = calendar?.imageUrl;
                    calendars << props;
                }
            }
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
            Map props = [:]
            Map calendar = FileSystem.load("${grailsApplication.config.models.path}/${id}.json")
            if(calendar){
                result = [status:"ok", calendar: calendar]
            } else{
                result = [status:"error", error: "Invalid id"]
            }
        }
        catch(Exception ex){
            log.error("Error loading calendars.");
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
            FileSystem.save(props, "${grailsApplication.config.models.path}/${id}.json")
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
            FileSystem.save(props, "${grailsApplication.config.models.path}/${id}.json")
            result = [status:'ok', calendarId: id]
        } catch(Exception exception) {
            result = [status: 'error', error: "Error saving the calendar, please try again later."]
        }

        render result as JSON;
    }

    @AlaSecured(value = ['ROLE_SC', 'ROLE_ADMIN'], anyRole = true)
    def delete(String id) {
        def props = request.JSON
        Map result
        try {
            props.calendarId = id
            props.calendarStatus = STATUS_DELETED;
            FileSystem.save(props, "${grailsApplication.config.models.path}/${id}.json")
            result = [status:'ok', calendarId: id]
        } catch(Exception exception) {
            result = [status: 'error', error: "Error saving the calendar, please try again later."]
        }

        render result as JSON;
    }
}
