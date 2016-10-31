package au.org.ala.calendar

import org.bson.types.ObjectId

class Calendar {
    ObjectId id
    String calendarId
    String multimedia
    String imageUrl
    String description
    String calendarName
    String calendarStatus
    String externalLink
    public static final String STATUS_DELETED = "deleted";

    public static final String STATUS_PUBLISHED = "published";


    List<Season> seasons


    static constraints = {
        multimedia nullable: true
        imageUrl nullable: true
        description nullable: true
        calendarName nullable: true
        calendarStatus nullable: true
        externalLink nullable: true
        seasons nullable: true
    }

    static embedded = ['seasons']

    static mapping = {
        name calendarId: [unique:true]
    }

    boolean equals(Object obj) {
        return super.equals(obj)
    }

    def beforeValidate() {
        if(!calendarId) {
            calendarId = UUID.randomUUID().toString()
        }
    }
}