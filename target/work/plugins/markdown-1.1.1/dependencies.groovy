grails.project.dependency.resolution = {
    inherits("global")
    repositories {
		grailsCentral()
        mavenCentral()
	}
    dependencies {
        compile 'org.jsoup:jsoup:1.6.1'
		compile 'org.apache.commons:commons-lang3:3.0.1'
		compile 'org.pegdown:pegdown:1.1.0'
    }
	plugins {
	   build ':release:2.2.1', ':rest-client-builder:1.0.3', {
	      export = false
	   }
	}
}