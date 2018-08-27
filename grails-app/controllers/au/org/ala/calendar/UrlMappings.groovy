package au.org.ala.calendar

class UrlMappings {

	static mappings = {

        get "/profile/search"(controller:'profile', action:'search')
        get "/opus"(controller:"opus", action:"index")
//        get "/opus/create"(controller:"opus", action:"create")
        post "/opus"(controller:"opus", action:"save")
        get "/opus/$id"(controller:"opus", action:"show")
//        get "/opus/$id/edit"(controller:"opus", action:"edit")
        put "/opus/$id"(controller:"opus", action:"update")
        delete "/opus/$id"(controller:"opus", action:"delete")

//        get "/opus/$opusId/profile"(controller:"profile", action:"index")
//        get "/opus/create"(controller:"opus", action:"create")
        post "/opus/$opusId/profile"(controller:"profile", action:"save")
        get "/opus/$opusId/profile/$id"(controller:"profile", action:"show")
//        get "/opus/$id/edit"(controller:"opus", action:"edit")
        put "/opus/$opusId/profile/$id"(controller:"profile", action:"update")
        delete "/opus/$opusId/profile/$id"(controller:"profile", action:"delete")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/"(view:"/index")
        "/index"(view:"/index")
        "500"(view:'/error')
	}
}
