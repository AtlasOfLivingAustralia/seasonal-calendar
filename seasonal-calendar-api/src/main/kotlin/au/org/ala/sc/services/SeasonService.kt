package au.org.ala.sc.services

import au.org.ala.jooq.postgres.Range
import au.org.ala.sc.api.SeasonDto
import au.org.ala.sc.domain.jooq.tables.Season.SEASON
import au.org.ala.sc.domain.jooq.tables.daos.SeasonDao
import au.org.ala.sc.domain.jooq.tables.pojos.Season
import org.jooq.Configuration
import org.jooq.DSLContext
import java.util.*

class SeasonService(
    private val seasonDao: SeasonDao,
    private val ctx: DSLContext,
    private val featureService: FeatureService
) {

    fun getSeasonsForCalendarId(collectionUuid: UUID): List<SeasonDto> {
        val seasons = seasonDao.fetchByCollectionUuid(collectionUuid)
        return seasons.map { season ->
            val dto = convertSeasonToDto(season)
            val features = season.profileUuids.map { profileUuid -> featureService.getFeature(collectionUuid, profileUuid) }
            dto.copy(features = features)
        }
    }

    fun getSeason(collectionUuid: UUID, name: String) =
        ctx.selectFrom(SEASON).where(SEASON.COLLECTION_UUID.eq(collectionUuid)).and(SEASON.LOCAL_NAME.eq(name)).fetchOne().into(Season::class.java)

    fun getSeasonWithFeatures(collectionUuid: UUID, name: String) {
        val season = getSeason(collectionUuid, name)
        val features = season.profileUuids.map{ featureService.getFeature(collectionUuid, it) }
        convertSeasonToDto(season).copy(features = features)
    }

    fun saveSeason(collectionUuid: UUID, dto: SeasonDto, txConfig: Configuration? = null) {

        if (txConfig != null) {
            internalSaveSeason(collectionUuid, dto, txConfig)
        } else {
            ctx.transaction { config ->
                internalSaveSeason(collectionUuid, dto, config)
            }
        }
    }

    private fun convertSeasonToDto(season: Season) =
        SeasonDto(
            id = season.id,
            localName = season.localName,
            alternateName = season.alternateName,
            startMonth = season.months.start,
            endMonth = season.months.end,
            weatherIcons = season.weatherIcon,
            description = season.description,
            features = emptyList()
        )

    private fun convertDtoToSeason(collectionUuid: UUID, dto: SeasonDto, featureIds: List<UUID>) =
            Season(
                dto.id,
                collectionUuid,
                dto.localName,
                dto.alternateName,
                Range(dto.startMonth, dto.endMonth),
                dto.weatherIcons,
                dto.description,
                featureIds.toTypedArray()
            )

    private fun internalSaveSeason(collectionUuid: UUID, dto: SeasonDto, txConfig: Configuration) {
        val features = dto.features.map { feature ->
            // would need to pass txConfig here if feature service required it
            return@map if (feature.profileUuid == null) {
                featureService.createFeature(collectionUuid, feature)
            } else {
                featureService.updateFeature(collectionUuid, feature)
                feature.profileUuid
            }
        }

        val txSeasonDao = SeasonDao(txConfig)

        if (dto.id != null && txSeasonDao.existsById(dto.id)) {
            val season = convertDtoToSeason(collectionUuid, dto, features)
            txSeasonDao.update(season)
        } else {
            val season = convertDtoToSeason(collectionUuid, dto, features)
            txSeasonDao.insert(season)
        }
    }


}