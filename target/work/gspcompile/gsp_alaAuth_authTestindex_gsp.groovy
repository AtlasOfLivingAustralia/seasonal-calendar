import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='ala-auth', version='1.3.2-SNAPSHOT')
class gsp_alaAuth_authTestindex_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/ala-auth-1.3.2-SNAPSHOT/grails-app/views/authTest/index.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',5,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("layout"),'content':("main")],-1)
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',6,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',6,[:],2)
printHtmlPart(3)
})
invokeTag('captureHead','sitemesh',9,[:],1)
printHtmlPart(4)
createTagBody(1, {->
printHtmlPart(5)
if(true && (flash.message)) {
printHtmlPart(6)
expressionOut.print(flash.message)
printHtmlPart(7)
}
printHtmlPart(8)
expressionOut.print(createLink(controller: 'authTest', action:'userList'))
printHtmlPart(9)
expressionOut.print(createLink(controller: 'authTest', action:'userDetailsSearch'))
printHtmlPart(10)
expressionOut.print(createLink(controller: 'authTest', action:'currentUserDetails'))
printHtmlPart(11)
invokeTag('loginLogout','hf',48,['cssClass':("btn")],-1)
printHtmlPart(12)
expressionOut.print(request.userPrincipal)
printHtmlPart(13)
})
invokeTag('captureBody','sitemesh',58,['class':("content")],1)
printHtmlPart(14)
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
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 9, 9, 11, 13, 13, 13, 13, 15, 15, 17, 17, 23, 23, 31, 31, 39, 39, 48, 48, 48, 48, 58, 58, 58, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "index.gsp"
)
class ___LineNumberPlaceholder { }
