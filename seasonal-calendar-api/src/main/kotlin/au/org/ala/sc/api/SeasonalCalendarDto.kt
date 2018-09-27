package au.org.ala.sc.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.NotEmpty

@JsonIgnoreProperties("tempKey")
data class SeasonalCalendarDto(
    val collectionUuid: String,
    @field:NotEmpty val name: String,
    @field:NotEmpty val shortName: String,
    val description: String,
    val imageUrl: String,
    val websiteUrl: String,
    val organisationName: String,
    val contributors: List<String>,
    val contactName: String,
    val contactEmail: String,
    val keywords: List<String>,
    val about: String,
    val organisationUrl: String,
    val organisationLogoUrl: String,
    val development: String,
    val references: List<String>,
    val referenceLinks: List<String>,
    val developmentReason: String,
    val limitations: String,
    val licenceTerms: String,
    val latitude: Double?,
    val longitude: Double?,
    val zoom: Int?,
    val languageGroup: String,
    val published: Boolean,
    val seasons: List<SeasonDto> = emptyList(),
    val welcomeCountry: String,
    val welcomeCountryMedia: String,
    val whoWeAreDescription: String,
    val ourCountry: String,
    val ourHistory: String,
    val logos: List<String>,
    val mediaLinks: List<String>,
    val externalLinks: List<String>
)

data class CalendarSavedDto(
    val collectionUuid: String
)