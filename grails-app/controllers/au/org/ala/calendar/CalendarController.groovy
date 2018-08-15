package au.org.ala.calendar

import au.org.ala.web.AlaSecured
import au.org.ala.web.UserDetails
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import org.apache.http.entity.ContentType

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.POST
import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.JSON
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

class CalendarController {


    CalendarService calendarService
    PermissionService permissionService
    UserService userService
    SearchService searchService
    WebService webService

    // Prototype.
    def ai(){
    }

    def imageIdentify() {
        def props = request.JSON
        String imageUrl = props?.imageUrl ?: ''
        def concepts = []
        if(imageUrl) {
            String bodyStr = '{"inputs": [{"data": {"image": {"url": "'+imageUrl+'"}}}]}'
            def connection = new URL("https://api.clarifai.com/v2/models/model_id_test2/outputs").openConnection() as HttpURLConnection
            connection.setRequestProperty('Authorization', 'Key f1b7288eb92b4981bab7b36791681aab')
            connection.setRequestProperty('Content-Type', 'application/json')
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)

            java.io.OutputStreamWriter wr = new java.io.OutputStreamWriter(connection.getOutputStream(), 'utf-8')
            wr.write(bodyStr)
            wr.flush()
            wr.close()

            def response = connection.inputStream.text
            def jsonSlurper = new JsonSlurper()
            Map object = jsonSlurper.parseText(response)
            log.debug("${connection.responseCode}: $response")
            concepts = object?.outputs?.data?.concepts
        }

        render ([status:'ok', aiResult: concepts] as JSON)
    }

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

    @AlaSecured()
    def myCalendars() {

        boolean onlyMyCalendars = true
        boolean userIsAdmin = userService.userIsScAdmin()
        String calendarManagementHome =  (userIsAdmin && !onlyMyCalendars) ? 'settings' : 'myCalendars'

        render view: 'settings', model: [id: params.id, onlyMyCalendars:onlyMyCalendars, userIsAdmin: userIsAdmin, calendarManagementHome: calendarManagementHome ]
    }

    @AlaSecured()
    def listMyCalendars() {
        def calendars;

        try{

            UserDetails userDetails = userService.getUser()
            List files = calendarService.listMyCalendars(userDetails?.userId)
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

    @AlaSecured(value = ['ROLE_ADMIN', 'ROLE_SC_ADMIN'], anyRole = true, redirectUri = '/')
    def settings() {
        boolean onlyMyCalendars = false
        boolean userIsAdmin = userService.userIsScAdmin()
        String calendarManagementHome =  (userIsAdmin && !onlyMyCalendars) ? 'settings' : 'myCalendars'

        return [id: params.id, onlyMyCalendars:onlyMyCalendars, userIsAdmin: userIsAdmin, calendarManagementHome: calendarManagementHome ]
    }

    @AlaSecured()
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

    @AlaSecured()
    @PreAuthorise()
    def editCalendar(String id) {
        def props = request.JSON

        try{
            render calendarService.update(id, props) as JSON;

        } catch(Exception e) {
            log.error("Error updating calendar", e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error saving the calendar, please try again later."
        }
    }

    @AlaSecured()
    @PreAuthorise()
    def delete(String id) {
        try {
            calendarService.delete(id)
            Map result = [calendarId: id]
            render result as JSON;

        } catch (Exception e) {
            log.error("Error updating calendar", e)
            render status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: "Error saving the calendar, please try again later."
        }

    }

    def searchBie(String q, Integer limit) {
        def results = searchService.searchBie(q ?: '' , limit ?: 10)
        // standardise output - Handle some unhelpful results from the BIE.
        results?.autoCompleteList?.removeAll { !it.name }

        results?.autoCompleteList?.each { result ->
            result.scientificName = result.name
            if (result.commonName && result.commonName.contains(',')) {
                // ?. doesn't use groovy truth so throws exception for JSON.NULL
                result.commonName = result.commonName.split(',')[0]
            }
        }
        log.debug('{}', results)
        render results as JSON
    }
}
