import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='ala-auth', version='1.3.2-SNAPSHOT')
class gsp_alaAuth_authTestcurrentUserDetails_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/ala-auth-1.3.2-SNAPSHOT/grails-app/views/authTest/currentUserDetails.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()

def authService = applicationContext.authService

printHtmlPart(0)
printHtmlPart(1)
createTagBody(1, {->
printHtmlPart(2)
invokeTag('captureMeta','sitemesh',8,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("layout"),'content':("main")],-1)
printHtmlPart(2)
createTagBody(2, {->
createClosureForHtmlPart(3, 3)
invokeTag('captureTitle','sitemesh',9,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',9,[:],2)
printHtmlPart(4)
})
invokeTag('captureHead','sitemesh',12,[:],1)
printHtmlPart(5)
createTagBody(1, {->
printHtmlPart(6)
expressionOut.print(authService.userId)
printHtmlPart(7)
expressionOut.print(authService.displayName)
printHtmlPart(8)
expressionOut.print(authService.email)
printHtmlPart(9)
expressionOut.print(authService.userDetails())
printHtmlPart(10)
})
invokeTag('captureBody','sitemesh',52,['class':("content")],1)
printHtmlPart(11)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1454279870000L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}

@org.codehaus.groovy.grails.web.transform.LineNumber(
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 12, 12, 14, 23, 23, 23, 23, 31, 31, 39, 39, 47, 47, 52, 52, 52, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "currentUserDetails.gsp"
)
class ___LineNumberPlaceholder { }
