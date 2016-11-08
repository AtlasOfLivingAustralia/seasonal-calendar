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

    public static final String STATUS_DELETED = "deleted"
    public static final String STATUS_PUBLISHED = "published"
    public static final String STATUS_UNPUBLISHED = "unpublished"

    List<Season> seasons


    static constraints = {
        calendarId nullable: false, unique: true
        calendarName nullable: false
        calendarStatus nullable: false
    }

    static embedded = ['seasons']

    boolean equals(Object obj) {
        return super.equals(obj)
    }

    def beforeValidate() {
        if(!calendarId) {
            calendarId = UUID.randomUUID().toString()
        }
    }
}