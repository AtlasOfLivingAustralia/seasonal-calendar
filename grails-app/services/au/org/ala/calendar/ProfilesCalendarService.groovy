package au.org.ala.calendar

import org.springframework.beans.factory.annotation.Autowired

class ProfilesCalendarService implements ICalendarService {

    static transactional = false

//    CommonService commonService
//    PermissionService permissionService
//    UserService userService

    def grailsApplication

    @Autowired
    ProfileServiceClient profileServiceClient

    def create(Map props) {
        throw new UnsupportedOperationException("TODO create")
    }

    def update(String id, Map props) {
        throw new UnsupportedOperationException("TODO update")
    }


    List<Calendar> list() {
        throw new UnsupportedOperationException("TODO list")
    }

    Calendar get(String calendarId) {
        throw new UnsupportedOperationException("TODO get(id)")
    }

    def delete(String calendarId) {
        throw new UnsupportedOperationException("TODO delete(id)")
    }

    /**
     * Find all calendars where the user represented by the userId has explicit permission to modify
     * @param userId The user id of the calendars owner
     * @return The list of calendars for the user
     */
    List listMyCalendars(String userId) {
        throw new UnsupportedOperationException("TODO list(userid)")
    }
}
