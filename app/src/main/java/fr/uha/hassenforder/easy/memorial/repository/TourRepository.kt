package fr.uha.hassenforder.easy.memorial.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao.GuideUpdate
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.dto.ManagedTour
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourWithGuides
import fr.uha.hassenforder.easy.memorial.model.Tour
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TourRepository(
    private val dispatcher: CoroutineDispatcher,
    private val tourDao: TourDao,
    private val guideDao : GuideDao,
    private val management: ManagementRepository<Tour, ManageableDao<Tour>>
) {

    fun getAll(): Flow<List<ManagedTour>> {
        return tourDao.getAll()
    }

    fun getFiltered(): Flow<List<ManagedTour>> {
        return tourDao.getFiltered()
    }

    fun getTourWithGuides(id: Long): Flow<ManagedTourWithGuides?> {
        return tourDao.getTourWithGuides(id)
    }

    @WorkerThread
    suspend fun update(dto : TourUpdate) = withContext(dispatcher) {
        when (dto) {
            is TourUpdate.Description -> tourDao.update(dto)
            is TourUpdate.Mode -> tourDao.update(dto)
            is TourUpdate.Picture -> tourDao.update(dto)
            is TourUpdate.Title -> tourDao.update(dto)
            is TourUpdate.WithDelay -> tourDao.update(dto)
            is TourUpdate.WithHint -> tourDao.update(dto)
            is TourUpdate.WithPicture -> tourDao.update(dto)
        }
    }

    @WorkerThread
    suspend fun updateGuidePosition(dto : List<GuideUpdate.Position>) = withContext(dispatcher) {
        guideDao.update(dto)
    }

    suspend fun deleteGuide(guide: FullGuide) = withContext(dispatcher) {
        guideDao.delete(guide.guide)
    }

    @WorkerThread
    suspend fun create(tour : Tour): Long = management.create(tour)

    @WorkerThread
    suspend fun valid(tid: Long, valid: Boolean) = management.valid (tid, valid)

    @WorkerThread
    suspend fun delete(tour : Tour) = management.delete(tour.tourId, tour)

    @WorkerThread
    suspend fun edit(tid: Long): Long = management.edit(tid)

    @WorkerThread
    suspend fun restore(tid: Long): Boolean = management.restore(tid)

    @WorkerThread
    suspend fun hide(tid: Long): Boolean = management.hide(tid)

    @WorkerThread
    suspend fun show(tid: Long): Boolean = management.show(tid)

}
