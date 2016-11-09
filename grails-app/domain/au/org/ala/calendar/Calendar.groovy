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
    String how
    String why
    String license
    String limitations
    String reference
    String referenceLink
    public static final String STATUS_DELETED = "deleted"
    public static final String STATUS_PUBLISHED = "published"
    public static final String STATUS_UNPUBLISHED = "unpublished"

    List seasons = []
    Map sites
    Organisation organisation

    static constraints = {
        calendarId nullable: false, unique: true
        calendarName nullable: false
        calendarStatus nullable: false

        multimedia nullable: true, blank:true
        imageUrl nullable: true, blank:true
        description nullable: true, blank:true
        calendarName nullable: true, blank:true
        calendarStatus nullable: true, blank:true
        externalLink nullable: true, blank:true

        how nullable: true, blank:true
        why nullable: true, blank:true
        license nullable: true, blank:true
        limitations nullable: true, blank:true
        reference nullable: true, blank:true
        referenceLink nullable: true, blank:true

        sites nullable: true
        seasons nullable: true
    }

    static embedded = ['organisation']

    boolean equals(Object obj) {
        return super.equals(obj)
    }

    def beforeValidate() {
        if(!calendarId) {
            calendarId = UUID.randomUUID().toString()
        }
    }
}