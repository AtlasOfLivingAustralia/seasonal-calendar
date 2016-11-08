package au.org.ala.calendar

class Category {
    String categoryName
    String speciesLink
    String speciesName
    String description

    List<String> images

    List<ThumbImage> thumbImages

    // Looks like constraints in embedded objects are just ignored
    static constraints = {
        categoryName nullable: false
        description nullable: false
    }

    static embedded = ['thumbImages']
}
