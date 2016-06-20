import org.pegdown.PegDownProcessor

class MarkdownGrailsPlugin {
    // the plugin version
    def version = "1.1.1"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.5 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def author = "Ted Naleid"
    def authorEmail = "contact@naleid.com"
    def title = "Grails Markdown Plugin"

    def developers = [
            [ name: "Phil DeJarnett" ]
    ]
    
    def description = '''\\
A Grails plugin to provide tag library and service support for markdown.  It can be used both for converting markdown into HTML, as well as converting HTML back into markdown.

You can either use the body of the tag to hold the markdown:

<markdown:renderHtml>This is a *test* of markdown.</markdown:renderHtml>

renders: 

<p>This is a <em>test</em> of markdown.</p>

Or the "text" attribute:

<markdown:renderHtml text="Yet **another** markdown test."/>

renders:

<p>Yet <strong>another</strong> markdown test.</p>

It also adds a <code>markdownToHtml()</code> method to the String class. 

See http://daringfireball.net/projects/markdown/basics for syntax basics

Thanks to @dani_latorre for the patch adding markdownToHtml functionality on the String class and Phil DeJarnett for significant changes/upgrades to Pegdown as well as the Remark html-markdown round trip functionality.


Full documentation can be seen on <a href="https://bitbucket.org/tednaleid/grails-markdown"> the bitbucket source repo</a>.
'''

    // URL to the plugin's documentation
    def documentation = "https://bitbucket.org/tednaleid/grails-markdown"

    def issueManagement = [ system: 'bitbucket', url: 'https://bitbucket.org/tednaleid/grails-markdown/issues' ]

	def scm = [ url: "https://bitbucket.org/tednaleid/grails-markdown" ]
    
	def doWithDynamicMethods = { ctx ->
		String.metaClass.markdownToHtml = {
			ctx.markdownService.markdown(delegate)
    	}
		
		String.metaClass.htmlToMarkdown = {
			ctx.markdownService.htmlToMarkdown(delegate)
    	}
		
		// allow backwards-compatible markdownService.processor.markdown(text)
		PegDownProcessor.metaClass.markdown = { text ->
			delegate.markdownToHtml(text.toString())
		}
    }
}
