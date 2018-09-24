package au.org.ala.sc.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

/*
Season field    |   Is required?    |   Collection/Profile field    |  Comments
Language or Local name  |   Mandatory   |   Local name  |
Alternate name (English name)   |   Optional    |   Alternate name  |   This was mandatory in the prototype, it should now be optional
English months  |   Mandatory   |   Months  |
Weather icons   |   Optional    |   Icon    |
Season description  |   Mandatory   |   Season description
*/
@JsonIgnoreProperties("tempKey")
data class SeasonDto(
    val id: Int?,
    @field:NotEmpty val localName: String,
    @field:NotEmpty val alternateName: String,
    @field:NotNull @field:Min(1) @field:Max(12) val startMonth: Int,
    @field:NotNull @field:Min(1) @field:Max(12) val endMonth: Int,
    val weatherIcons: String?,
    @field:NotEmpty val description: String,
    val features: List<FeatureDto> = emptyList()
)