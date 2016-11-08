package au.org.ala.calendar

class Season {

    String seasonName
    String months
    String weatherIcon
    String description
    List<Category> categories


    // Looks like constraints in embedded objects are just ignored
    static constraints = {
        seasonName nullable: false
        months nullable: false
        description nullable: false
    }

    static embedded = ['categories']
}
