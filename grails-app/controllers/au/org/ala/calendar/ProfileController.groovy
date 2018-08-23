package au.org.ala.calendar

import au.org.ala.profiles.service.Profile
import au.org.ala.profiles.service.ProfileServiceClient
import com.squareup.moshi.Moshi
import grails.web.http.HttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

import static org.springframework.http.HttpStatus.*

class ProfileController {

    static allowedMethods = [save: "POST", update: ["PUT", "POST"], patch: "PATCH", delete: "DELETE"]

    @Autowired
    ProfileServiceClient profileServiceClient
    @Autowired
    Moshi moshi
    def authService

    Class<Profile> resource = Profile.class
    String resourceName = 'Profile'
    String resourceClassName = resource.simpleName
    boolean readOnly = false

//    /**
//     * Lists all resources up to the given maximum
//     *
//     * @param max The maximum
//     * @return A list of resources
//     */
//    def index(Integer max) {
//        params.max = Math.min(max ?: 10, 100)
//        respond listAllResources(params), model: [("ProfileCount".toString()): countResources()]
//    }

    /**
     * Shows a single resource
     * @param id The id of the resource
     * @return The rendered resource or a 404 if it doesn't exist
     */
    def show() {
        respond queryForResource(params.opusId, params.id)
    }

    /**
     * Displays a form to create a new resource
     */
    def create() {
        if(handleReadOnly()) {
            return
        }
        respond createResource()
    }

    /**
     * Saves a resource
     */
    def save() {
        if(handleReadOnly()) {
            return
        }
        def instance = createResource()

//        instance.validate()
//        if (instance.hasErrors()) {
//            transactionStatus.setRollbackOnly()
//            respond instance.errors, view:'create' // STATUS CODE 422
//            return
//        }

        saveResource params.opusId, instance

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [classMessageArg, instance.id])
                redirect instance
            }
            '*' {
                response.addHeader(HttpHeaders.LOCATION,
                        grailsLinkGenerator.link( resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                respond instance, [status: CREATED, view:'show']
            }
        }
    }

    def edit() {
        if(handleReadOnly()) {
            return
        }
        respond queryForResource(params.opusId, params.id)
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    def patch() {
        update()
    }

    /**
     * Updates a resource for the given id
     * @param id
     */
    def update() {
        if(handleReadOnly()) {
            return
        }

        Profile instance = createResource()

//        if (instance.hasErrors()) {
//            transactionStatus.setRollbackOnly()
//            respond instance.errors, view:'edit' // STATUS CODE 422
//            return
//        }

        updateResource params.opusId, instance
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [classMessageArg, instance.id])
                redirect instance
            }
            '*'{
                response.addHeader(HttpHeaders.LOCATION,
                        grailsLinkGenerator.link( resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
                                namespace: hasProperty('namespace') ? this.namespace : null ))
                send instance
            }
        }
    }

    /**
     * Deletes a resource for the given id
     * @param id The id
     */
    def delete() {
        if(handleReadOnly()) {
            return
        }

        def instance = queryForResource(params.opusId, params.id)

        deleteResource params.opusId, instance

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [classMessageArg, instance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT } // NO CONTENT STATUS CODE
        }
    }

    /**
     * handles the request for write methods (create, edit, update, save, delete) when controller is in read only mode
     *
     * @return true if controller is read only
     */
    protected boolean handleReadOnly() {
        if(readOnly) {
            render status: HttpStatus.METHOD_NOT_ALLOWED.value()
            return true
        } else {
            return false
        }
    }

    /**
     * Queries for a resource for the given id
     *
     * @param id The id
     * @return The resource or null if it doesn't exist
     */
    protected Profile queryForResource(String opusId, String id) {
        def call = profileServiceClient.getProfile(opusId, id, authService.userId)
        call.execute().body()
    }

    /**
     * Creates a new instance of the resource for the given parameters
     *
     * @param params The parameters
     * @return The resource instance
     */
    protected Profile createResource(Map params) {
        moshi.adapter(Profile).fromJson(request.reader.text)
    }

    /**
     * Creates a new instance of the resource.  If the request
     * contains a body the body will be parsed and used to
     * initialize the new instance, otherwise request parameters
     * will be used to initialized the new instance.
     *
     * @return The resource instance
     */
    protected Profile createResource() {
        request.contentLength > 0 ? moshi.adapter(Profile).fromJson(request.reader.text) : Profile.builder().build()
    }

//    /**
//     * List all of resource based on parameters
//     *
//     * @return List of resources or empty if it doesn't exist
//     */
//    protected List<Profile> listAllResources(Map params) {
//        def call = profileServiceClient.getProfiles('calendar', authService.userId)
//        call.execute().body()
//    }

    /**
     * Counts all of resources
     *
     * @return List of resources or empty if it doesn't exist
     */
    protected Integer countResources() {
        0
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [classMessageArg, params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

    /**
     * Saves a resource
     *
     * @param resource The resource to be saved
     * @return The saved resource or null if can't save it
     */
    protected Profile saveResource(String opusId, Profile resource) {
        def call = resource.uuid ? profileServiceClient.updateProfile(opusId, resource.uuid, authService.userId, resource) : profileServiceClient.createProfile(opusId, authService.userId, resource)
        call.execute()
        // TODO createOpus returns id?
        return resource
    }

    /**
     * Updates a resource
     *
     * @param resource The resource to be updated
     * @return The updated resource or null if can't save it
     */
    protected Profile updateResource(String opusId, Profile resource) {
        saveResource opusId, resource
    }

    /**
     * Deletes a resource
     *
     * @param resource The resource to be deleted
     */
    protected void deleteResource(String opusId, Profile resource) {
        profileServiceClient.deleteProfile(opusId, resource.uuid, authService.userId)
    }

    protected String getClassMessageArg() {
        message(code: "${resourceName}.label".toString(), default: resourceClassName)
    }

    protected void send(object) {
        gson.toJson(object, response.writer)
    }

}
