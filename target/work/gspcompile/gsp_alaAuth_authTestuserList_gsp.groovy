import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='ala-auth', version='1.3.2-SNAPSHOT')
class gsp_alaAuth_authTestuserList_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/ala-auth-1.3.2-SNAPSHOT/grails-app/views/authTest/userList.gsp" }
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
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(5)
for( user in (authService.allUserNameList) ) {
printHtmlPart(6)
expressionOut.print(user.userId)
printHtmlPart(7)
expressionOut.print(user.userName)
printHtmlPart(7)
expressionOut.print(user.displayName)
printHtmlPart(8)
}
printHtmlPart(9)
})
invokeTag('captureBody','sitemesh',35,['class':("nav-datasets")],1)
printHtmlPart(10)
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
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 4, 4, 8, 8, 8, 9, 9, 9, 9, 9, 9, 9, 12, 12, 13, 25, 25, 25, 25, 27, 27, 28, 28, 29, 29, 31, 31, 35, 35, 35, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "userList.gsp"
)
class ___LineNumberPlaceholder { }
