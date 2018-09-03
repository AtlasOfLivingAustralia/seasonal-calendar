package au.org.ala.sc.services

import au.org.ala.jooq.postgres.Range
import au.org.ala.profiles.service.ProfileServiceClient
import au.org.ala.sc.api.SeasonDto
import au.org.ala.sc.domain.jooq.Tables
import au.org.ala.sc.domain.jooq.tables.Season.SEASON
import au.org.ala.sc.domain.jooq.tables.daos.SeasonDao
import au.org.ala.sc.domain.jooq.tables.pojos.Season
import au.org.ala.sc.domain.jooq.tables.records.SeasonRecord
import org.jooq.DSLContext
import org.slf4j.LoggerFactory
import java.util.*

class SeasonService(
    val seasonDao: SeasonDao,
    val ctx: DSLContext,
    val featureService: FeatureService,
    val profileServiceClient: ProfileServiceClient
) {
    companion object {
        val log = LoggerFactory.getLogger(SeasonService::class.java)
    }

    fun getSeasonsForCalendarId(collectionUuid: UUID) =
        seasonDao.fetchByCollectionUuid(collectionUuid).map(this::convertSeasonToDto)

    fun getSeason(collectionUuid: UUID, name: String) =
        ctx.selectFrom(SEASON).where(SEASON.COLLECTION_UUID.eq(collectionUuid)).and(SEASON.LOCAL_NAME.eq(name)).fetchOne().into(Season::class.java)

//    private fun getId(collectionUuid: UUID, name: String) =
//        ctx.newRecord(SEASON.COLLECTION_UUID, SEASON.LOCAL_NAME).values(collectionUuid, name)

    fun getSeasonWithFeatures(collectionUuid: UUID, name: String) {
        val season = getSeason(collectionUuid, name)
        val profiles = season.profileUuids.map{ featureService.getFeature(collectionUuid, it) }
        convertSeasonToDto(season).copy(features = profiles)
    }

    fun convertSeasonToDto(season: Season) =
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

    fun convertDtoToSeason(collectionUuid: UUID, dto: SeasonDto) =
            Season(
                dto.id,
                collectionUuid,
                dto.localName,
                dto.alternateName,
                Range(dto.startMonth, dto.endMonth),
                dto.weatherIcons,
                dto.description,
                dto.features.map { it.profileUuid }.filterNotNull().toTypedArray()
            )

    fun saveSeason(collectionUuid: UUID, dto: SeasonDto) {
        ctx.transaction { ->

//            val season = if (dto.id != null) seasonDao.findById(dto.id) else null
            if (seasonDao.existsById(dto.id)) {
                val season = convertDtoToSeason(collectionUuid, dto)
                seasonDao.update(season)
            } else {
                val season = convertDtoToSeason(collectionUuid, dto)
                seasonDao.insert(season)
            }
            dto.features.forEach { feature ->
                featureService.saveFeature(collectionUuid, feature)
            }
        }
    }


}