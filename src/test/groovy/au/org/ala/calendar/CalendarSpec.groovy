package au.org.ala.calendar

import com.github.fakemongo.Fongo
import com.mongodb.MongoClient
import grails.test.mixin.TestFor
import grails.test.mongodb.MongoSpec
import grails.util.ClosureToMapPopulator
import grails.validation.ConstraintsEvaluator
import org.grails.validation.ConstraintsEvaluatorFactoryBean

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Calendar)
class CalendarSpec extends MongoSpec {

    @Override
    MongoClient createMongoClient() {
        return new Fongo("seasonal-calendar-test").mongo
    }

    @Override
    protected List<Class> getDomainClasses() {
        [Calendar]
    }

    def setup() {
    }


    def setupSpec(){

        // Set up grails.gorm.default.constraints as in Config.groovy as there is a bug in Grails that prevents it
        // Make sure the constraint below always reflect the same value.
        // More details https://github.com/grails/grails-data-mapping/issues/543
//        defineBeans {
//            "${ConstraintsEvaluator.BEAN_NAME}"(ConstraintsEvaluatorFactoryBean) {
//                def constraintsClosure = {
//                    '*'(nullable: true, size: 1..64)
//                }
//                defaultConstraints = new ClosureToMapPopulator().populate(constraintsClosure)
//            }
//        }
    }


    def cleanup() {
    }

    void "test required fields in a new Calendar"() {
        given:"valid fields are missing"
        Calendar calendar = new Calendar(description: "Optional field")

        when: "Saving"
        calendar.save()

        def errors = calendar?.errors?.allErrors

        then: "An error is produced"
        assert errors

        List<String> errorMessages = errors.collect {
            it as String
        }

        assert errorMessages.find( {it.contains("calendarName")})
        assert errorMessages.find( {it.contains("calendarStatus")})

    }

    // Constraints in embedded objects are not applied not even while running the whole application: Grails Fail!
//    void "test required fields in a new Season"() {
//        given:"valid fields are missing"
//        Calendar calendar = new Calendar(calendarName: "Calendar Name", calendarStatus: "unpublished")
//
//        Season season = new Season()
//        calendar.seasons = [season]
//
//        when: "Saving"
//        calendar.save(flush: true)
//
//        def errors = season?.errors?.allErrors
//
//        then: "An error is produced"
//        assert errors
//
//        List<String> errorMessages = errors.collect {
//            it as String
//        }
//
//        assert errorMessages.find( {it.contains("seasonName")})
//        assert errorMessages.find( {it.contains("months")})
//        assert errorMessages.find( {it.contains("description")})
//
//    }
}
