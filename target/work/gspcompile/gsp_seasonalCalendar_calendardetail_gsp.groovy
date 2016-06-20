import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_seasonalCalendar_calendardetail_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/calendar/detail.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',4,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("layout"),'content':("egret-sc")],-1)
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',5,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',5,[:],2)
printHtmlPart(3)
createTagBody(2, {->
printHtmlPart(4)
expressionOut.print(grailsApplication.config.grails.serverURL)
printHtmlPart(5)
expressionOut.print(id)
printHtmlPart(6)
expressionOut.print(createLink(controller: 'calendar', action: 'settings'))
printHtmlPart(7)
expressionOut.print(createLink(controller: 'calendar', action: 'addCalendar'))
printHtmlPart(8)
expressionOut.print(createLink(controller: 'calendar', action: 'editCalendar'))
printHtmlPart(9)
expressionOut.print(createLink(controller: 'calendar', action: 'getCalendar'))
printHtmlPart(10)
expressionOut.print(createLink(controller: 'calendar', action: 'delete'))
printHtmlPart(11)
expressionOut.print(createLink(controller: 'calendar', action: 'listCalendars'))
printHtmlPart(12)
})
invokeTag('script','r',18,['disposition':("head")],2)
printHtmlPart(1)
invokeTag('require','r',19,['modules':("seasonal_calendar")],-1)
printHtmlPart(13)
})
invokeTag('captureHead','sitemesh',20,[:],1)
printHtmlPart(14)
createTagBody(1, {->
printHtmlPart(15)
invokeTag('render','g',54,['template':("detailFlatView")],-1)
printHtmlPart(16)
createClosureForHtmlPart(17, 2)
invokeTag('script','r',196,[:],2)
printHtmlPart(18)
})
invokeTag('captureBody','sitemesh',199,[:],1)
printHtmlPart(19)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1466149918000L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}

@org.codehaus.groovy.grails.web.transform.LineNumber(
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 5, 5, 5, 5, 5, 5, 7, 9, 9, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 18, 18, 19, 19, 20, 20, 20, 22, 54, 54, 54, 54, 196, 196, 199, 199, 199, 199, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "detail.gsp"
)
class ___LineNumberPlaceholder { }
