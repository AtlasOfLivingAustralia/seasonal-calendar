package au.org.ala

import au.org.ala.calendar.UserService
import au.org.ala.web.AuthService
import groovy.xml.MarkupBuilder

class SCTagLib {

    static namespace = "sc"

//    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    AuthService authService
    UserService userService

    def currentUserDisplayName = { attrs, body ->
        def username = authService.getDisplayName()
        if (username) {
            def mb = new MarkupBuilder(out)
            mb.span(class:'username') {
                mkp.yield(username)
            }
        }
    }


    /**
     * Is the user a SC Admin
     */
    def ifUserisScAdmin = { attrs, body ->
        if (userService.userIsScAdmin()) out << body()
    }

    /**
     * Is the user a SC Admin
     */
    def ifUserisNotScAdmin = { attrs, body ->
        if (!userService.userIsScAdmin()) out << body()
    }

}
