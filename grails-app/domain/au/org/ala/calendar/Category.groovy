package au.org.ala.calendar

class Category {
    String categoryName
    String speciesLink
    String speciesName
    String description

    List<String> images

    List<ThumbImage> thumbImages

    static constraints = {
    }

    static embedded = ['thumbImages']
}
