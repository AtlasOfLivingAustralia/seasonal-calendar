package au.org.ala.calendar

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class CalendarService {

    static transactional = false

    CommonService commonService
    PermissionService permissionService
    UserService userService


    Calendar create(Map props) {
        try {
            if (props.calendarId && Calendar.findByCalendarId(props.calendarId)) {
                // clear session to avoid exception when GORM tries to autoflush the changes
                Calendar.withSession { session -> session.clear() }
                throw new IllegalArgumentException("Duplicate calendar id ${props.calendarId}")
            }
            // name is a mandatory property and hence needs to be set before dynamic properties are used (as they trigger validations)
            Calendar calendar = new Calendar(props)
            // Not flushing on create was causing that further updates to fields were overriden by old values
            calendar.save(flush: true, failOnError: true)

            permissionService.addUserAsAdminToCalendar(userService.getUser().userId, calendar.calendarId)

            return calendar
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            log.error("Error creating calendar ${props.calendarId}", e)
            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }
            throw new Exception(error,e)
        }
    }

    def update(Map props) {
        try {
            Calendar storedCalendar =  Calendar.findByCalendarId(props.calendarId)
            if(storedCalendar) {

                def seasonsMap = props.remove('seasons')
                if(seasonsMap) {
                    List<Season> seasons = []
                    seasonsMap.each {
                        seasons << new Season(it)
                    }
                    storedCalendar.seasons = seasons
                }
                commonService.updateProperties(storedCalendar, props)
            } else {
                throw new IllegalArgumentException("No such id ${props.calendarId}")
            }
        } catch (Exception e) {
            def error = "Error updating calendar - ${e.message}"
            log.error("Error updating calendar ${props.calendarId}", e)
            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            throw new Exception(error,e)
        }
    }


    List<Calendar> list() {
        Calendar.findAllByCalendarStatusNotEqual(Calendar.STATUS_DELETED)
    }

    Calendar get(String calendarId) {
        Calendar.findByCalendarId(calendarId)
    }

    def delete(String calendarId) {
        try {
            Calendar storedCalendar =  Calendar.findByCalendarId(calendarId)
            if(storedCalendar) {
                storedCalendar.calendarStatus = Calendar.STATUS_DELETED

                // Not flushing on create was causing that further updates to fields were overriden by old values
                storedCalendar.save(flush: true, failOnError: true)
                return
            } else {
                throw new IllegalArgumentException( "No such id ${calendarId}")
            }
        } catch (Exception e) {
            def error = "Error deleting calendar - ${e.message}"
            log.error("Error deleting calendar ${calendarId}", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }
            throw new Exception(error,e)
        }
    }
}
