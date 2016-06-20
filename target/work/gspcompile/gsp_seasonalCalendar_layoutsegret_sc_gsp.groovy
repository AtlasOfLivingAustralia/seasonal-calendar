import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_seasonalCalendar_layoutsegret_sc_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/layouts/egret-sc.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createTagBody(2, {->
createTagBody(3, {->
invokeTag('layoutTitle','g',4,[:],-1)
})
invokeTag('captureTitle','sitemesh',4,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',4,[:],2)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',5,['gsp_sm_xmlClosingForEmptyTag':(""),'charset':("utf-8")],-1)
printHtmlPart(2)
invokeTag('captureMeta','sitemesh',7,['gsp_sm_xmlClosingForEmptyTag':(""),'name':("viewport"),'content':("width=device-width, initial-scale=1.0")],-1)
printHtmlPart(3)
expressionOut.print(request.contextPath)
printHtmlPart(4)
invokeTag('require','r',24,['modules':("sc_calendar")],-1)
printHtmlPart(1)
invokeTag('layoutResources','r',25,[:],-1)
printHtmlPart(5)
})
invokeTag('captureHead','sitemesh',26,[:],1)
printHtmlPart(5)
createTagBody(1, {->
printHtmlPart(6)
expressionOut.print(request.contextPath)
printHtmlPart(7)
createTagBody(2, {->
printHtmlPart(8)
expressionOut.print(grailsApplication.config.casServerLoginUrl)
printHtmlPart(9)
expressionOut.print(grailsApplication.config.serverName)
expressionOut.print(request.forwardURI)
printHtmlPart(10)
})
invokeTag('ifNotLoggedIn','auth',66,[:],2)
printHtmlPart(11)
createTagBody(2, {->
printHtmlPart(12)
expressionOut.print(request.contextPath)
printHtmlPart(13)
expressionOut.print(grailsApplication.config.grails.serverURL)
printHtmlPart(14)
expressionOut.print(grailsApplication.config.casServerUrlPrefix)
printHtmlPart(15)
expressionOut.print(grailsApplication.config.grails.serverURL)
printHtmlPart(16)
})
invokeTag('ifLoggedIn','auth',75,[:],2)
printHtmlPart(17)
invokeTag('layoutBody','g',88,[:],-1)
printHtmlPart(18)
invokeTag('layoutResources','r',119,[:],-1)
printHtmlPart(5)
})
invokeTag('captureBody','sitemesh',120,[:],1)
printHtmlPart(19)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1466150760000L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}

@org.codehaus.groovy.grails.web.transform.LineNumber(
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 7, 7, 7, 17, 17, 24, 25, 25, 26, 26, 26, 27, 56, 56, 56, 56, 61, 61, 61, 61, 61, 61, 61, 66, 66, 67, 68, 68, 68, 68, 70, 70, 70, 70, 70, 70, 75, 75, 75, 88, 88, 119, 120, 120, 120, 120, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "egret-sc.gsp"
)
class ___LineNumberPlaceholder { }
