package au.org.ala.calendar

class ThumbImage {

    String url

    // Looks like constraints in embedded objects are just ignored
    static constraints = {
        url nullable: false
    }
}
