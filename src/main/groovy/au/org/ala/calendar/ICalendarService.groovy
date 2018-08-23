package au.org.ala.calendar

interface ICalendarService {

    def create(Map props)

    def update(String id, Map props)

    List<Calendar> list()

    Calendar get(String calendarId)

    def delete(String calendarId)

    /**
     * Find all calendars where the user represented by the userId has explicit permission to modify
     * @param userId The user id of the calendars owner
     * @return The list of calendars for the user
     */
    List listMyCalendars(String userId)

}
