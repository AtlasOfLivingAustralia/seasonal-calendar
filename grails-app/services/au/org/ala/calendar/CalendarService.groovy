package au.org.ala.calendar

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class CalendarService {

    static transactional = false

    CommonService commonService

    def create(Map props) {
        try {
            if (props.calendarId && Calendar.findByCalendarId(props.calendarId)) {
                // clear session to avoid exception when GORM tries to autoflush the changes
                Calendar.withSession { session -> session.clear() }
                return [status: 'error', error: 'Duplicate calendar id for create ' + props.calendarId]
            }
            // name is a mandatory property and hence needs to be set before dynamic properties are used (as they trigger validations)
            Calendar calendar = new Calendar(props)
            // Not flushing on create was causing that further updates to fields were overriden by old values
            calendar.save(flush: true, failOnError: true)

            return [status: 'ok', calendarId: calendar.calendarId]
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            log.error("Error creating calendar. ", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            return [status: 'error', error: error]
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

                // Not flushing on create was causing that further updates to fields were overriden by old values
//                calendar.save(flush: true, failOnError: true)
                return [status: 'ok', calendarId: storedCalendar.calendarId]
            } else {
                def error = "Error updating calendar - no such id ${props.calendarId}"
                log.error error
                return [status: 'error', error: error]
            }
        } catch (Exception e) {
            def error = "Error updating calendar ${props.calendarId} - ${e}"
            log.error("Error creating calendar. ", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            return [status: 'error', error: error]
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
                return [status: 'ok', calendarId: calendarId]
            } else {
                def error = "Error deleting calendar - no such id ${calendarId}"
                log.error error
                return [status: 'error', error: error]
            }
        } catch (Exception e) {
            def error = "Error updating calendar ${calendarId} - ${e}"
            log.error("Error creating calendar. ", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            return [status: 'error', error: error]
        }
    }
}
