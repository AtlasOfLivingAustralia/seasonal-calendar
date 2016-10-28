package au.org.ala.calendar

import grails.converters.JSON
import grails.transaction.Transactional

@Transactional
class CalendarService {

    static transactional = false

    CommonService commonService

    def create(Map props, boolean collectoryLink, boolean overrideUpdateDate = false) {
        try {
            if (props.calendarId && Calendar.findByCalendarId(props.calendarId)) {
                // clear session to avoid exception when GORM tries to autoflush the changes
                Calendar.withSession { session -> session.clear() }
                return [status: 'error', error: 'Duplicate calendar id for create ' + props.calendarId]
            }
            // name is a mandatory property and hence needs to be set before dynamic properties are used (as they trigger validations)
            Calendar calendar = new Calendar(calendarId: props.calendarId, calendarName: props.calendarName, calendarStatus: 'UNPUBLISHED')
            // Not flushing on create was causing that further updates to fields were overriden by old values
            calendar.save(flush: true, failOnError: true)

            props.remove('calendarId')


//            if (collectoryLink) {
//                establishCollectoryLinkForProject(calendar, props)
//            }

            commonService.updateProperties(calendar, props, overrideUpdateDate)
            return [status: 'ok', calendarId: calendar.calendarId]
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            logger.error("Error creating calendar. ", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            return [status: 'error', error: error]
        }
    }

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

            props.remove('calendarId')


            return [status: 'ok', calendarId: calendar.calendarId]
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            logger.error("Error creating calendar. ", e)

            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }

            return [status: 'error', error: error]
        }
    }

    def update(Map props) {
        try {
            Calendar storedCalendar =  Calendar.findByCalendarId(props.calendarId)
            if(storedCalendar) {
                // name is a mandatory property and hence needs to be set before dynamic properties are used (as they trigger validations)
                Calendar calendar = new Calendar(props)
                // Not flushing on create was causing that further updates to fields were overriden by old values
                calendar.save(flush: true, failOnError: true)
                return [status: 'ok', calendarId: calendar.calendarId]
            }
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            logger.error("Error creating calendar. ", e)

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
}
