package au.org.ala.calendar

import grails.converters.JSON

class AccessFilters {
    def grailsApplication

    PermissionService permissionService
    UserService userService
    CalendarService calendarService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                def controller = grailsApplication.getArtefactByLogicalPropertyName("Controller", controllerName)
                Class controllerClass = controller?.clazz
                def method = controllerClass?.getMethod(actionName?:"index", [] as Class[])

                Map result = [error: '', status : 401]

                // ALA Auth api already looks after validating AlaSecured annotation so there is no need to check
                // that here

                if (controllerClass?.isAnnotationPresent(PreAuthorise) || method?.isAnnotationPresent(PreAuthorise)) {
                    // Prevent any access if not explicitly granted.
                    result = [error: 'Permission not granted for this operation', status : 401]

                    // What rules needs to be satisfied?
                    PreAuthorise pa = method.getAnnotation(PreAuthorise) ?: controllerClass.getAnnotation(PreAuthorise)

                    if (pa.basicAuth()) {
                        request.userId = userService.getUser()?.userId
                        if(userService.userIsAlaAdmin()) {
                            /* Don't enforce check for ALA admin.*/
                            result.error = ''
                        }
                        else if (request.userId) {
                            String accessLevel = pa.accessLevel()
                            String idType = pa.idType()
                            String entityId = params[pa.id()]

                            if (accessLevel && idType) {

                                switch (idType) {
                                    case "calendarId":
                                        def calendar = calendarService.get(entityId)
                                        result = permissionService.checkPermission(accessLevel, calendar?.calendarId, Calendar.class.name, request.userId)
                                        break
                                    default:
                                        break
                                }
                            }
                        } else {
                            result.error = "Access denied"
                            result.status = 401
                        }
                    }

                }

                if(result.error) {
                    render  status: result.status, text: result.error
                    return false
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
