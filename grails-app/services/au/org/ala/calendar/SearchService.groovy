package au.org.ala.calendar

import au.org.ala.ws.service.WebService

class SearchService {
    WebService webService
    def grailsApplication
    static transactional = false

    def searchBie(searchTerm, limit) {
        if (!limit) {
            limit = 10
        }
        def encodedQuery = URLEncoder.encode(searchTerm ?: '', "UTF-8")
        def url = "${grailsApplication.config.bie.url}/ws/search/auto.jsonp?q=${encodedQuery}&limit=${limit}&idxType=TAXON"

        webService.get(url)
    }
}
