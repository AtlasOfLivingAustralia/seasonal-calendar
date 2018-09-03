package au.org.ala.sc.services

import au.org.ala.profiles.service.*
import au.org.ala.sc.api.FeatureDto
import org.slf4j.LoggerFactory
import java.util.*

class FeatureService(
    val profileServiceClient: ProfileServiceClient
) {

    companion object {
        val log = LoggerFactory.getLogger(FeatureService::class.java)
        const val userId = "8373"
    }

    fun getFeature(collectionUuid: UUID, profileUuid: UUID) : FeatureDto {
        val getProfileResponse = profileServiceClient.getProfile(collectionUuid.toString(), profileUuid.toString(), userId).execute()
        if (getProfileResponse.isSuccessful) {
            val profile = getProfileResponse.body()!!
            return convertProfileToFeatureDto(profile)
        } else {
            log.error("Couldn't retrieve getProfileResponse collection uuid {}, profile uuid {} with code {}", collectionUuid, profileUuid, getProfileResponse.code())
            throw FeatureException("Couldn't retrieve getProfileResponse collection uuid $collectionUuid, profile uuid $profileUuid with code ${getProfileResponse.code()}")
        }
    }

    fun saveFeature(collectionUuid: UUID, feature: FeatureDto) {
        if (feature.profileUuid == null) {
            profileServiceClient.createProfile(collectionUuid.toString(), userId, convertFeatureDtoToProfile(feature))
        } else {
            val profileUuid = feature.profileUuid.toString()
            val getProfileResponse = profileServiceClient.getProfile(collectionUuid.toString(), profileUuid.toString(), userId).execute()
            if (getProfileResponse.isSuccessful) {
                val profile = getProfileResponse.body()!!
                profileServiceClient.updateProfile(collectionUuid.toString(), profileUuid, userId, profile.applyFeatureToProfile(feature))
            } else {
                log.error("Couldn't retrieve getProfileResponse collection uuid {}, profile uuid {} with code {}", collectionUuid, profileUuid, getProfileResponse.code())
                throw FeatureException("Couldn't retrieve getProfileResponse collection uuid $collectionUuid, profile uuid $profileUuid with code ${getProfileResponse.code()}")
            }
        }
    }

    fun convertFeatureDtoToProfile(feature: FeatureDto) =
        Profile().applyFeatureToProfile(feature)

    fun Profile.applyFeatureToProfile(feature: FeatureDto) : Profile {
        fullName = feature.name
        (attributes.find { it.title.name == "commonName" } ?: createAttribute().also { attributes.add(it) }).updateAttribute("commonName", feature.name)
        scientificName = feature.scientificName
        (attributes.find { it.title.name == "description" } ?: createAttribute().also { attributes.add(it) }).updateAttribute("description", feature.name)
        privateImages = feature.imageUrls.map { url -> LocalImage().apply { imageId = url } }
        return this
    }

    fun createAttribute() =
        Attribute().apply {
            title = Term()
        }

    fun Attribute.updateAttribute(name: String, value: String) = this.apply {
        text = value
        title.name = name
    }

    fun convertProfileToFeatureDto(profile: Profile) =
        FeatureDto(
            profileUuid = UUID.fromString(profile.uuid),
            name = profile.fullName,
            commonName = profile.attributes.find { it.title.name == "commonName" }?.text,
            scientificName = profile.scientificName,
            description = profile.attributes.find { it.title.name == "description" }?.text ?: "",
            imageUrls = profile.privateImages.map { it.imageId }
        )

}

class FeatureException(message: String) : RuntimeException(message)
