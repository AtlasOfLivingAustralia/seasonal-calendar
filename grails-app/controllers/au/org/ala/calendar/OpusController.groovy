package au.org.ala.calendar

import au.org.ala.profiles.service.Opus
import au.org.ala.profiles.service.ProfileServiceClient
import com.squareup.moshi.Moshi
import grails.web.http.HttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

import static okio.Okio.buffer
import static okio.Okio.sink
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

class OpusController {

    static allowedMethods = [save: "POST", update: ["PUT", "POST"], patch: "PATCH", delete: "DELETE"]

    @Autowired
    ProfileServiceClient profileServiceClient
    @Autowired
    Moshi moshi
    def authService

    Class<Opus> resource = Opus.class
    String resourceName = 'Opus'
    String resourceClassName = resource.simpleName
    boolean readOnly = false

    /**
     * Lists all resources up to the given maximum
     *
     * @param max The maximum
     * @return A list of resources
     */
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond listAllResources(params), model: [("OpusCount".toString()): countResources()]
    }

    /**
     * Shows a single resource
     * @param id The id of the resource
     * @return The rendered resource or a 404 if it doesn't exist
     */
    def show() {
        respond queryForResource(params.id)
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

        saveResource instance

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
        respond queryForResource(params.id)
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

        Opus instance = createResource()

//        if (instance.hasErrors()) {
//            transactionStatus.setRollbackOnly()
//            respond instance.errors, view:'edit' // STATUS CODE 422
//            return
//        }

        updateResource instance
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

        def instance = queryForResource(params.id)

        deleteResource instance

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
    protected Opus queryForResource(String id) {
        def call = profileServiceClient.getOpus(id, authService.userId)
        def callResponse = call.execute()

        if (callResponse.successful) {
            return callResponse.body()
        } else {
            log.error("Couldn't find $id, service returned ${callResponse.code()}")
            return null
        }
    }

    /**
     * Creates a new instance of the resource for the given parameters
     *
     * @param params The parameters
     * @return The resource instance
     */
    protected Opus createResource(Map params) {
        moshi.adapter(Opus).fromJson(request.reader.text)
    }

    /**
     * Creates a new instance of the resource.  If the request
     * contains a body the body will be parsed and used to
     * initialize the new instance, otherwise request parameters
     * will be used to initialized the new instance.
     *
     * @return The resource instance
     */
    protected Opus createResource() {
        request.contentLength > 0 ? moshi.adapter(Opus).fromJson(request.reader.text) : Opus.builder().build();
    }

    /**
     * List all of resource based on parameters
     *
     * @return List of resources or empty if it doesn't exist
     */
    protected List<Opus> listAllResources(Map params) {
        def call = profileServiceClient.getOperaByTag('IEK', authService.userId, params)
        def callResponse = call.execute()
        if (callResponse.successful) {
            callResponse.body()
        } else {
            log.error("Couldn't retrieve list of all opera, service returned ${callResponse.code()}")
            []
        }
    }

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
    protected Opus saveResource(Opus resource) {
        def call = resource.uuid ? profileServiceClient.updateOpus(resource.uuid, authService.userId, resource) : profileServiceClient.createOpus(authService.userId, resource)
        def callResponse = call.execute()
        if (!callResponse.successful) {
            throw new RuntimeException("Couldn't save collection")
        }
        return resource
    }

    /**
     * Updates a resource
     *
     * @param resource The resource to be updated
     * @return The updated resource or null if can't save it
     */
    protected Opus updateResource(Opus resource) {
        saveResource resource
    }

    /**
     * Deletes a resource
     *
     * @param resource The resource to be deleted
     */
    protected void deleteResource(Opus resource) {
        profileServiceClient.deleteOpus(resource.uuid, authService.userId)
    }

    protected String getClassMessageArg() {
        message(code: "${resourceName}.label".toString(), default: resourceClassName)
    }

    protected void send(object) {
        response.contentType = 'application/json'
        response.characterEncoding = 'UTF-8'
        moshi.adapter(object.getClass()).toJson(buffer(sink(response.outputStream)), object)
    }

}
