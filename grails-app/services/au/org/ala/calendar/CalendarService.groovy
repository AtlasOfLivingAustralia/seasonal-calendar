package au.org.ala.calendar

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.transaction.Transactional

class CalendarService implements ICalendarService {

    static transactional = false

    GrailsApplication grailsApplication

    GormCalendarService gormCalendarService
    ProfilesCalendarService profilesCalendarService

    def getCalendarService() {
        if (grailsApplication.config.backing.store == 'profiles') {
            return profilesCalendarService
        } else {
            return gormCalendarService
        }
    }

    def create(Map props) {
        getCalendarService().create(props)
    }

    def update(String id, Map props) {
        getCalendarService().update(id,props)
    }


    List<Calendar> list() {
        getCalendarService().list()
    }

    Calendar get(String calendarId) {
        getCalendarService().get(calendarId)
    }

    def delete(String calendarId) {
        getCalendarService().delete(calendarId)
    }

    /**
     * Find all calendars where the user represented by the userId has explicit permission to modify
     * @param userId The user id of the calendars owner
     * @return The list of calendars for the user
     */
    List listMyCalendars(String userId) {
        getCalendarService().listMyCalendars(userId)
    }
}
