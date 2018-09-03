package au.org.ala.sc.api

/*
Season field    |   Is required?    |   Collection/Profile field    |  Comments
Language or Local name  |   Mandatory   |   Local name  |
Alternate name (English name)   |   Optional    |   Alternate name  |   This was mandatory in the prototype, it should now be optional
English months  |   Mandatory   |   Months  |
Weather icons   |   Optional    |   Icon    |
Season description  |   Mandatory   |   Season description
*/
data class SeasonDto(
    val id: Int?,
    val localName: String,
    val alternateName: String?,
    val startMonth: Int,
    val endMonth: Int,
    val weatherIcons: String?,
    val description: String,
    val features: List<FeatureDto> = emptyList()
)