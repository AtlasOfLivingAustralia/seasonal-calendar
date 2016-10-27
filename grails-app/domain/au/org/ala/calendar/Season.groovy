package au.org.ala.calendar

class Season {

    String seasonName
    String months
    String weatherIcon
    String description
    List<Category> categories


    static constraints = {
    }

    static embedded = ['categories']
}
