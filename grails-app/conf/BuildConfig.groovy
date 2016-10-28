import grails.util.Environment

grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.server.port.http = 8088

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: false, // [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: false, // [maxMemory: 768, minMemory: 512, debug: true, maxPerm: 256],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: false// [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]
grails.project.dependency.resolver = "maven"

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "none"
grails.views.gsp.encoding = "UTF-8"

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        mavenLocal()
        mavenRepo ("http://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
    }

    plugins {
        build ":release:3.1.1"
        build ":tomcat:7.0.54"
        compile ":markdown:1.1.1"
        compile ":ala-auth:1.3.4"
        compile ':cache:1.1.3'
        compile ":cache-headers:1.1.6"
        compile ":ala-ws-plugin:1.0"
        runtime ":resources:1.2.14"
        if (Environment.current == Environment.PRODUCTION) {
            runtime ":cached-resources:1.0"
        }
    }
}
