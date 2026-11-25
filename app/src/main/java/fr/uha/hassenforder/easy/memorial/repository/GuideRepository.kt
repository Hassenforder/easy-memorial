package fr.uha.hassenforder.easy.memorial.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao.GuideUpdate
import fr.uha.hassenforder.easy.memorial.database.dao.PoiDao
import fr.uha.hassenforder.easy.memorial.database.dao.PoiDao.PoiUpdate
import fr.uha.hassenforder.easy.memorial.database.dao.TriggerDao
import fr.uha.hassenforder.easy.memorial.database.dao.TriggerDao.TriggerUpdate
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.model.Guide
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GuideRepository(
    private val dispatcher: CoroutineDispatcher,
    private val guideDao: GuideDao,
    private val poiDao: PoiDao,
    private val triggerDao: TriggerDao,
) {

    fun getById(id: Long): Flow<FullGuide?> {
        return guideDao.getFullGuideById(id)
    }

    @WorkerThread
    suspend fun create(guide : FullGuide): Long = withContext(dispatcher)  {
        poiDao.create(guide.poi)
        triggerDao.create(guide.trigger)
        val filledGuide : Guide = guide.guide.copy (
            poiId = guide.poi.poiId,
            triggerId = guide.trigger.triggerId,
        )
        guideDao.create(filledGuide)
        return@withContext filledGuide.guideId
    }

    @WorkerThread
    suspend fun update (dto : GuideUpdate) {
        when(dto) {
            is GuideUpdate.Position -> guideDao.update(dto)
        }
    }

    @WorkerThread
    suspend fun update (dto : PoiUpdate) {
        when(dto) {
            is PoiUpdate.Name -> poiDao.update(dto)
            is PoiUpdate.Description -> poiDao.update(dto)
            is PoiUpdate.Picture -> poiDao.update(dto)
            is PoiUpdate.Media -> poiDao.update(dto)
        }
    }

    @WorkerThread
    suspend fun update (dto : TriggerUpdate) {
        when(dto) {
            is TriggerUpdate.Label -> triggerDao.update(dto)
            is TriggerUpdate.Latitude -> triggerDao.update(dto)
            is TriggerUpdate.Longitude -> triggerDao.update(dto)
            is TriggerUpdate.Hint -> triggerDao.update(dto)
        }
    }

    @WorkerThread
    suspend fun delete(guide: FullGuide) = withContext(dispatcher) {
        if (guide.poi.poiId != 0L) {
            poiDao.delete(guide.poi)
        }
        if (guide.trigger.triggerId != 0L) {
            triggerDao.delete(guide.trigger)
        }
        guideDao.delete(guide.guide)
    }

}
