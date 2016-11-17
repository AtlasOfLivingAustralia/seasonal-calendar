package au.org.ala.calendar

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotation to check if user has valid permissions on the given entity passed in a Controller action
 *
 */
@Target([ElementType.TYPE, ElementType.METHOD])
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorise {
    String id() default "id" // The parameter holding the id value
    boolean basicAuth() default true      // Check username against authKey?
    String accessLevel() default "editor" // What is the minimum access level needed to access the method?
    String idType() default "calendarId" // The entity Id name
}
