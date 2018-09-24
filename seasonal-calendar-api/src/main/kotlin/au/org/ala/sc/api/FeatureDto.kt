package au.org.ala.sc.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.NotEmpty
import java.util.*

/*
Feature field | Profile field | Comments

Feature name | Profile name |
English name | Profile attribute – English name | Or could just be the matched species name (currenly not working????)
Feature species | (matched name)
Feature Description | Profile attribute – Description
Feature images | (selected images) | * can include uploaded private images (although how to add these from the SC interface is a question).
 */
@JsonIgnoreProperties("tempKey")
data class FeatureDto(
    val profileUuid: UUID?,
    @field:NotEmpty val name: String,
    @field:NotEmpty val commonName: String,
    val scientificName: String?,
    val scientificNameGuid: String?,
    @field:NotEmpty val description: String,
    val imageUrls: List<String>
)