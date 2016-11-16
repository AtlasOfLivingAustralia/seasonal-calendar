/**
 * Created by mol109 on 15/11/2016.
 */

eventCreateWarStart = { warName, stagingDir ->
    ant.propertyfile(file: "${stagingDir}/WEB-INF/classes/application.properties") {
        entry(key:"app.build", value: new Date().format("dd/MM/yyyy HH:mm:ss"))
    }
}