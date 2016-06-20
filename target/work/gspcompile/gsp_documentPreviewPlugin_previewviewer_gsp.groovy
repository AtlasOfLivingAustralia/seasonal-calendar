import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

@GrailsPlugin(name='document-preview-plugin', version='0.1-SNAPSHOT')
class gsp_documentPreviewPlugin_previewviewer_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/plugins/document-preview-plugin-0.1-SNAPSHOT/grails-app/views/preview/viewer.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',22,['gsp_sm_xmlClosingForEmptyTag':(""),'charset':("utf-8")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',23,['gsp_sm_xmlClosingForEmptyTag':(""),'name':("viewport"),'content':("width=device-width, initial-scale=1, maximum-scale=1")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',24,['gsp_sm_xmlClosingForEmptyTag':(""),'name':("google"),'content':("notranslate")],-1)
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',25,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',25,[:],2)
printHtmlPart(3)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/cmaps'))
printHtmlPart(4)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/images'))
printHtmlPart(5)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/', file: 'pdf.worker.js'))
printHtmlPart(6)
expressionOut.print(g.resource(dir:'vendor/pdfjs/1.1.215/viewer/', file: 'viewer.css'))
printHtmlPart(7)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/', file:'compatibility.js'))
printHtmlPart(8)
expressionOut.print(g.resource(dir:'vendor/pdfjs/1.1.215/viewer/locale/', file: 'locale.properties'))
printHtmlPart(9)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/', file:'l10n.js'))
printHtmlPart(10)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/', file:'pdf.js'))
printHtmlPart(11)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/', file:'debugger.js'))
printHtmlPart(10)
expressionOut.print(g.resource(dir: 'vendor/pdfjs/1.1.215/viewer/', file:'viewer.js'))
printHtmlPart(12)
})
invokeTag('captureHead','sitemesh',50,[:],1)
printHtmlPart(13)
createClosureForHtmlPart(14, 1)
invokeTag('captureBody','sitemesh',422,['tabindex':("1"),'class':("loadingInProgress")],1)
printHtmlPart(15)
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
	lines = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 22, 22, 22, 23, 23, 24, 24, 25, 25, 25, 25, 25, 25, 25, 33, 33, 34, 34, 35, 35, 38, 38, 40, 40, 43, 43, 44, 44, 45, 45, 47, 47, 48, 48, 50, 50, 52, 422, 422, 422, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	sourceName = "viewer.gsp"
)
class ___LineNumberPlaceholder { }
