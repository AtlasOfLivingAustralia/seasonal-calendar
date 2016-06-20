package com.naleid.grails

class MarkdownTagLib {
	static namespace = "markdown"
	def markdownService
	
    def renderHtml = { attrs, body ->
		def text
		if(attrs.template) {
			text = g.render(template: attrs.template)
		} else {
        	text = attrs.text ?: body()
		}
        out << markdownService.markdown(text.toString())
    }	
}
