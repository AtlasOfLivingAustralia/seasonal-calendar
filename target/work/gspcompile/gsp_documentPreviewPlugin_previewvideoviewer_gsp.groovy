import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='document-preview-plugin', version='0.1-SNAPSHOT')
class gsp_documentPreviewPlugin_previewvideoviewer_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/document-preview-plugin-0.1-SNAPSHOT/grails-app/views/preview/videoviewer.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',4,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',4,[:],2)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',5,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("viewport"),'content':("width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no")],-1)
printHtmlPart(3)
expressionOut.print(g.resource(dir: 'vendor/video-js/4.12.11/', file: 'video-js.css'))
printHtmlPart(4)
expressionOut.print(g.resource(dir: 'vendor/video-js/4.12.11/', file: 'video.js'))
printHtmlPart(5)
expressionOut.print(g.resource(dir: 'vendor/video-js/4.12.11/', file: 'video-js.swf'))
printHtmlPart(6)
})
invokeTag('captureHead','sitemesh',46,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
expressionOut.print(params.file)
printHtmlPart(9)
expressionOut.print(params.contentType)
printHtmlPart(10)
})
invokeTag('captureBody','sitemesh',55,[:],1)
printHtmlPart(11)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1460681508000L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}

@org.codehaus.groovy.grails.web.transform.LineNumber(
	lines = [55, 55, 55, 55, 55, 55, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 8, 8, 10, 10, 14, 14, 46, 46, 47, 50, 50, 50, 50, 51, 51, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55],
	sourceName = "videoviewer.gsp"
)
class ___LineNumberPlaceholder { }
