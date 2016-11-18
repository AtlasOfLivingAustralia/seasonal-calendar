package au.org.ala.calendar

import grails.converters.JSON
import grails.transaction.Transactional

class CalendarService {

    static transactional = false

    CommonService commonService
    PermissionService permissionService
    UserService userService


    def create(Map props) {
        try {

            props.remove('calendarId')
            props.calendarStatus = props.calendarStatus ?: Calendar.STATUS_UNPUBLISHED

            Calendar calendar = new Calendar(calendarId: UUID.randomUUID().toString())
            getCommonService().updateProperties(calendar, props)

            permissionService.addUserAsAdminToCalendar(userService.getUser().userId, calendar.calendarId)

            return [status:'ok',id: calendar.calendarId]
        } catch (Exception e) {
            def error = "Error creating calendar - ${e.message}"
            log.error("Error creating calendar ${props.calendarId}", e)
            // clear session to avoid exception when GORM tries to autoflush the changes
            Calendar.withSession { session -> session.clear() }
            throw new Exception(error,e)
        }
    }

    def update(String id, Map props) {
        try {
            Calendar calendar =  Calendar.findByCalendarId(id)
            if(calendar) {
                props.remove('calendarId')

                commonService.updateProperties(calendar, props)

                return [status:'ok',id:calendar.calendarId]
            } else {
                throw new IllegalArgumentException("No such id ${calendar.calendarId}")
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

    /**
     * Find all calendars where the user represented by the userId has explicit permission to modify
     * @param userId The user id of the calendars owner
     * @return The list of calendars for the user
     */
    List listMyCalendars(String userId) {
        List<UserPermission> userPermissions = UserPermission.
                findAllByUserIdAndEntityTypeAndAccessLevelInList(userId, Calendar.class.name, [AccessLevel.admin, AccessLevel.editor])

        List<String> calendarIds = userPermissions.collect {
            it.entityId
        }

        Calendar.findAllByCalendarIdInListAndCalendarStatusNotEqual(calendarIds,Calendar.STATUS_DELETED)
    }
}
