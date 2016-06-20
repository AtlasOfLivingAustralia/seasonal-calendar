package au.org.ala.calendar

/**
 * Created by sat01a on 14/06/16.
 */
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import static groovy.io.FileType.FILES

class FileSystem {
    static save(Object content, String filePath) {
        new File(filePath).write(new JsonBuilder(content).toPrettyString())
    }

    static Object load(String filePath) {
        return new JsonSlurper().parseText(new File(filePath).text)
    }

    static List getFiles(String dirPath, String extension = ".json"){
        def list = []

        def dir = new File(dirPath)
        dir.eachFileRecurse(FILES) { file ->
            if(file.name.endsWith(extension)) {
                list << file
            }
        }

        list
    }
}
