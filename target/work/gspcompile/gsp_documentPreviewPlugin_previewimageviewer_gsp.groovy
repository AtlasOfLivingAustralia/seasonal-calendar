import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='document-preview-plugin', version='0.1-SNAPSHOT')
class gsp_documentPreviewPlugin_previewimageviewer_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/document-preview-plugin-0.1-SNAPSHOT/grails-app/views/preview/imageviewer.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',5,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',5,[:],2)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',6,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("viewport"),'content':("width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no")],-1)
printHtmlPart(1)
invokeTag('require','r',7,['module':("resourceLeaflet")],-1)
printHtmlPart(1)
invokeTag('layoutResources','r',8,['disposition':("head")],-1)
printHtmlPart(3)
})
invokeTag('captureHead','sitemesh',22,[:],1)
printHtmlPart(4)
createTagBody(1, {->
printHtmlPart(5)
createClosureForHtmlPart(6, 2)
invokeTag('script','r',85,[:],2)
printHtmlPart(4)
invokeTag('layoutResources','r',86,['disposition':("defer")],-1)
printHtmlPart(4)
})
invokeTag('captureBody','sitemesh',87,[:],1)
printHtmlPart(7)
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
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5, 5, 5, 5, 6, 6, 7, 7, 8, 8, 8, 22, 22, 23, 26, 26, 85, 85, 86, 86, 87, 87, 87, 87, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "imageviewer.gsp"
)
class ___LineNumberPlaceholder { }
