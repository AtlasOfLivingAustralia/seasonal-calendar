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

    List<Season> seasons
    Organisation organisation
    Map sites

    static constraints = {
        calendarId nullable: false, unique: true
        calendarName nullable: false
        calendarStatus nullable: false

        how nullable: true
        why nullable: true
        license nullable: true
        limitations nullable: true
        reference nullable: true
        referenceLink nullable: true

        seasons nullable: true
        organisation nullable: true
        sites nullable: true
    }

    static embedded = ['seasons', 'organisation']

    boolean equals(Object obj) {
        return super.equals(obj)
    }

    def beforeValidate() {
        if(!calendarId) {
            calendarId = UUID.randomUUID().toString()
        }
    }
}