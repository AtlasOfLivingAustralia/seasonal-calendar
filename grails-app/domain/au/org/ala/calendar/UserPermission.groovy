package au.org.ala.calendar

import org.bson.types.ObjectId

/**
 * Domain class to store permissions settings on a User/Project
 * level.
 * @see AccessLevel
 */
class UserPermission {
    ObjectId id
    String userId
    String entityId
    AccessLevel accessLevel
    String entityType
    String status = Status.ACTIVE

    static constraints = {
        userId(unique: ['accessLevel', 'entityId']) // prevent duplicate entries

        entityId nullable: false
        status nullable: false
        accessLevel  nullable: false
        entityType nullable: false
    }
}
