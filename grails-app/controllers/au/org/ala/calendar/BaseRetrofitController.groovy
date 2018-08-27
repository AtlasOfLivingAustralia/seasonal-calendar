package au.org.ala.calendar

import retrofit2.Call

abstract class BaseRetrofitController {

    protected def proxy(Call<?> call) {
        try {
            def callResponse = call.execute()
            if (callResponse.successful) {
                respond callResponse.body()
            } else {
                response.sendError(callResponse.code(), callResponse.errorBody().string())
            }
        } catch (IOException e) {
            log.error("Exception executing $call", e)
            response.sendError(500)
        }
    }
}
