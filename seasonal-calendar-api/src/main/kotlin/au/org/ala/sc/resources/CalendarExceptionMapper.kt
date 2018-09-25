package au.org.ala.sc.resources

import au.org.ala.sc.services.CalendarException
import au.org.ala.sc.services.CalendarNotFoundException
import io.dropwizard.jersey.errors.ErrorMessage
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider

@Provider
class CalendarExceptionMapper : ExceptionMapper<CalendarException> {

    override fun toResponse(exception: CalendarException): Response {
        val status = when (exception) {
            is CalendarNotFoundException -> Response.Status.NOT_FOUND
            else -> Response.Status.INTERNAL_SERVER_ERROR
        }
        return Response
            .status(status)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(ErrorMessage(status.statusCode, exception.localizedMessage))
            .build()
    }
}