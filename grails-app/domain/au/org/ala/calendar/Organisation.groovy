package au.org.ala.calendar

class Organisation {

    String name
    String orgDescription
    String keywords
    String contactName
    String logo
    String contributors
    String url
    String email

    static constraints = {
        name nullable: true, blank: true
        orgDescription nullable: true, blank: true
        keywords nullable: true, blank: true
        contactName nullable: true, blank: true
        logo nullable: true, blank: true
        contributors nullable: true, blank: true
        url nullable: true, blank: true
        email nullable: true, blank: true
    }

}
