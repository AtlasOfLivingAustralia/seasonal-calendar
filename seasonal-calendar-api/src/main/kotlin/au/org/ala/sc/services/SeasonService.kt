package au.org.ala.sc.services

import au.org.ala.jooq.postgres.Range
import au.org.ala.sc.api.SeasonDto
import au.org.ala.sc.auth.User
import au.org.ala.sc.domain.jooq.tables.Season.SEASON
import au.org.ala.sc.domain.jooq.tables.daos.SeasonDao
import au.org.ala.sc.domain.jooq.tables.pojos.Season
import org.jooq.Configuration
import java.util.*

typealias SeasonServiceFactory = (user: User, configuration: Configuration?) -> DefaultSeasonService

class DefaultSeasonServiceFactory(private val defaultConfiguration: Configuration, private val featureServiceFactory: FeatureServiceFactory) : SeasonServiceFactory {
    override fun invoke(user: User, configuration: Configuration?): DefaultSeasonService = build(user, configuration)

    fun build(user: User, configuration: Configuration?) = DefaultSeasonService(configuration ?: defaultConfiguration, featureServiceFactory(user), user)
}

interface SeasonService {
    fun getSeasonsForCalendarId(collectionUuid: UUID): List<SeasonDto>
    fun getSeason(collectionUuid: UUID, name: String): Season?
    fun getSeasonWithFeatures(collectionUuid: UUID, name: String)
    fun saveSeason(collectionUuid: UUID, dto: SeasonDto)
}

interface TransactionableSeasonService: SeasonService, Transactionable<TransactionableSeasonService>

data class DefaultSeasonService(
    override val configuration: Configuration,
    private val featureService: FeatureService,
    private val user: User
) : TransactionableSeasonService {

    private val seasonDao by lazy(LazyThreadSafetyMode.NONE) { SeasonDao(configuration) }

    override fun getSeasonsForCalendarId(collectionUuid: UUID): List<SeasonDto> {
        val seasons = seasonDao.fetchByCollectionUuid(collectionUuid)
        return seasons.map { season ->
            val dto = convertSeasonToDto(season)
            val features = season.profileUuids.map { profileUuid -> featureService.getFeature(collectionUuid, profileUuid) }
            dto.copy(features = features)
        }
    }

    override fun getSeason(collectionUuid: UUID, name: String) =
        configuration.dsl()
            .selectFrom(SEASON)
            .where(SEASON.COLLECTION_UUID.eq(collectionUuid)).and(SEASON.LOCAL_NAME.eq(name))
            .fetchOne().into(Season::class.java)

    override fun getSeasonWithFeatures(collectionUuid: UUID, name: String) {
        val season = getSeason(collectionUuid, name)
        val features = season.profileUuids.map{ featureService.getFeature(collectionUuid, it) }
        convertSeasonToDto(season).copy(features = features)
    }

    override fun saveSeason(collectionUuid: UUID, dto: SeasonDto) {
        internalSaveSeason(collectionUuid, dto)
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

    private fun internalSaveSeason(collectionUuid: UUID, dto: SeasonDto) {
        val features = dto.features.map { feature ->
            return@map if (feature.profileUuid == null) {
                featureService.createFeature(collectionUuid, feature)
            } else {
                featureService.updateFeature(collectionUuid, feature)
                feature.profileUuid
            }
        }

        val season = convertDtoToSeason(collectionUuid, dto, features)
        if (dto.id != null && seasonDao.existsById(dto.id)) {
            seasonDao.update(season)
        } else {
            seasonDao.insert(season)
        }
    }

    override fun withTransaction(configuration: Configuration) =
            this.copy(configuration = configuration)
//        DefaultSeasonService(configuration, featureService, user)

}