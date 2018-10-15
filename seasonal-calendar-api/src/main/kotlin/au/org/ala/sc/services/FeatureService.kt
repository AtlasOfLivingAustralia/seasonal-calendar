package au.org.ala.sc.services

import au.org.ala.profiles.service.*
import au.org.ala.sc.api.FeatureDto
import au.org.ala.sc.auth.User
import au.org.ala.sc.util.HTTP_NOT_FOUND
import au.org.ala.sc.util.logger
import java.util.*

typealias FeatureServiceFactory = (user: User) -> FeatureService

class DefaultFeatureServiceFactory(private val profileServiceClient: ProfileServiceClient): FeatureServiceFactory {
    override fun invoke(user: User) = build(user)

    fun build(user: User) = DefaultFeatureService(profileServiceClient, user)
}

interface FeatureService {
    /**
     * Get a Feature DTO for a give calendar uuid and the feature / profile uuid
     * @param collectionUuid The uuid of the seasonal calendar / opus
     * @param profileUuid The uuid of the associated profile
     */
    fun getFeature(collectionUuid: UUID, profileUuid: UUID) : FeatureDto

    /**
     * Create a new feature for the given collection uuid
     * @param collectionUuid The uuid of the seasonal calendar
     * @param feature The feature to create
     * @return The id of the new feature
     */
    fun createFeature(collectionUuid: UUID, feature: FeatureDto): UUID

    /**
     * Update an existing feature for a given calendar uuid.
     * @param collectionUuid The collection uuid
     * @param feature The feature dto, including the existing uuid.
     * @return The newly constructed feature DTO
     */
    fun updateFeature(collectionUuid: UUID, feature: FeatureDto)
}

class DefaultFeatureService(
    private val profileServiceClient: ProfileServiceClient,
    private val user: User = User.Anonymous
) : FeatureService {

    companion object {
        val log = logger()
    }

    override fun getFeature(collectionUuid: UUID, profileUuid: UUID) : FeatureDto {
        return convertProfileToFeatureDto(getProfile(collectionUuid, profileUuid))
    }

    override fun createFeature(collectionUuid: UUID, feature: FeatureDto): UUID {
        val opusId = collectionUuid.toString()
        val profile = convertFeatureDtoToNewProfile(feature).apply { opusUuid = opusId }
        val profileResponse = profileServiceClient.createProfile(opusId, user.name, profile).execute()
        return if (profileResponse.isSuccessful) {
            val newProfile = profileResponse.body()!!
            for ((name, value) in listOf("commonName" to feature.commonName, "description" to feature.description)) {
                val attr = createNewAttribute(name, value)
                val attrResponse = profileServiceClient.createAttribute(opusId, newProfile.uuid, user.name, attr).execute()
                if (!attrResponse.isSuccessful) {
                    log.error("Couldn't create attribute $name, $value for ${newProfile.uuid}: ${newProfile.scientificName} because ${attrResponse.code()} ${attrResponse.errorBody()?.string()}")
                    throw FeatureException("Couldn't create attribute $name, $value for ${newProfile.scientificName}")
                }
            }
            UUID.fromString(newProfile.uuid)
        } else {
            log.error("Couldn't create profile for collection uuid {}. HTTP code: {}, body: {}", collectionUuid, profileResponse.code(), profileResponse.errorBody()?.string())
            throw FeatureException("Couldn't create profile $collectionUuid: ${feature.name}")
        }
    }

    override fun updateFeature(collectionUuid: UUID, feature: FeatureDto) {
        val profileUuid = feature.profileUuid.toString()
        val profile = getProfile(collectionUuid, feature.profileUuid!!)

        if (feature.name != profile.scientificName || feature.scientificNameGuid != profile.matchedName?.guid) {
            renameProfile(collectionUuid, profile, feature)
        }

        syncAttributes(collectionUuid, profile, feature)

        val updatedProfile = profile.applyFeatureToProfileUpdate(feature)
        // TODO check if an update is needed
        val response = profileServiceClient.updateProfile(collectionUuid.toString(), profileUuid, user.name, updatedProfile).execute()
        if (!response.isSuccessful) {
            log.error("Couldn't update profile for collection uuid {}, profile uuid {}. HTTP code: {}, body: {}", response.code(), response.errorBody()?.string())
            throw FeatureException("Couldn't update profile collection uuid $collectionUuid, profile uuid $profileUuid with code ${response.code()}")
        }
    }

    private fun syncAttributes(collectionUuid: UUID, profile: Profile, feature: FeatureDto) {
        val collectionUuidString = collectionUuid.toString()
        val attrsByName = profile.attributes.associateBy { it.title }
        for ((name, value) in listOf("commonName" to feature.commonName, "description" to feature.description)) {
            val attr = attrsByName[name]
            if (attr != null && attr.text != value) {
                val update = AttributeUpdate().apply { text = value }
                val response = profileServiceClient.updateAttribute(collectionUuidString, profile.uuid, attr.uuid, user.name, update).execute()
                if (!response.isSuccessful) {
                    log.error("Couldn't update attribute ${attr.title}")
                    throw FeatureException("Couldn't update attribute $name for $collectionUuid ${profile.uuid}")
                }
            } else if (attr == null) {
                val newAttr = createNewAttribute(name, value)
                profileServiceClient.createAttribute(collectionUuidString, profile.uuid, user.name, newAttr)

            }
        }
    }

    private fun renameProfile(collectionUuid: UUID, profile: Profile, feature: FeatureDto) {
        val renameRequest = RenameProfileRequest().apply {
            newName = feature.name
            manuallyMatchedGuid = feature.scientificNameGuid ?: ""
            clearMatch = feature.scientificNameGuid.isNullOrBlank()
        }
        val response = profileServiceClient.renameProfile(collectionUuid.toString(), profile.uuid, user.name, renameRequest).execute()
        if (!response.isSuccessful) {
            log.error("Couldn't rename profile from ${profile.scientificName} to ${feature.name} because ${response.code()}: ${response.errorBody()?.string()}")
            throw FeatureException("Couldn't rename profile from ${profile.scientificName} to ${feature.name}")
        }
    }

    private fun getProfile(collectionUuid: UUID, profileUuid: UUID): Profile {
        val response = profileServiceClient.getProfile(collectionUuid.toString(), profileUuid.toString(), user.name).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            val code = response.code()
            if (code == HTTP_NOT_FOUND) {
                throw FeatureNotFoundException(collectionUuid, profileUuid)
            } else {
                log.error("Couldn't retrieve getProfileResponse collection uuid {}, profile uuid {} with code {}", collectionUuid, profileUuid, response.code())
                throw FeatureException("Couldn't retrieve getProfileResponse collection uuid $collectionUuid, profile uuid $profileUuid with code ${response.code()}")
            }
        }
    }

    private fun convertFeatureDtoToNewProfile(feature: FeatureDto) =
            Profile().applyFeatureToNewProfile(feature)


    private fun Profile.applyFeatureToNewProfile(feature: FeatureDto) : Profile {
        scientificName = feature.name
//        fullName = feature.name
        manuallyMatchedGuid = feature.scientificNameGuid
//        if (matchedName == null) matchedName = Name()
//        matchedName.guid = feature.scientificNameGuid
//        matchedName.scientificName = feature.scientificName
//        isManuallyMatchedName = !(feature.scientificName.isNullOrBlank() && feature.scientificNameGuid.isNullOrBlank())
        privateImages = feature.imageUrls.map { url -> LocalImage().apply { imageId = url } }
        return this
    }

    private fun Profile.applyFeatureToProfileUpdate(feature: FeatureDto) : Profile {
//        scientificName = feature.name
//        fullName = feature.name
//        if (matchedName == null) matchedName = Name()
//        matchedName.guid = feature.scientificNameGuid
//        matchedName.scientificName = feature.scientificName
//        isManuallyMatchedName = !(feature.scientificName.isNullOrBlank() && feature.scientificNameGuid.isNullOrBlank())
//        attributes.updateValue("commonName", feature.commonName ?: "")
//        attributes.updateValue("description", feature.description)
        privateImages = feature.imageUrls.map { url -> LocalImage().apply { imageId = url } }
        return this
    }

    private fun MutableList<Attribute>.textValue(name: String) =
        find { it.title == name }?.text ?: ""

    private fun createNewAttribute(name: String, value: String?) =
        AttributeUpdate().apply {
//            uuid = UUID.randomUUID().toString() // TODO profiles won't create this for us?  Or we need to insert this using a webservice first?
            title = name
            userId = userId
            userDisplayName = "Simon Bear"
            text = value ?: ""
        }

    private fun convertProfileToFeatureDto(profile: Profile) =
        FeatureDto(
            profileUuid = UUID.fromString(profile.uuid),
            name = profile.scientificName,
            commonName = profile.attributes.textValue("commonName"),
            scientificName = profile.matchedName?.scientificName ?: "",
            scientificNameGuid = profile.matchedName?.guid ?: "",
            description = profile.attributes.textValue("description"),
            imageUrls = profile.privateImages.map { it.imageId }
        )

}

class FeatureNotFoundException(val collectionUuid: UUID, val profileUuid: UUID) : CalendarException("Couldn't find feature $profileUuid in calendar $collectionUuid")
class FeatureException(message: String) : CalendarException(message)
