package fr.uha.hassenforder.easy.memorial.repository

import androidx.annotation.WorkerThread
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.android.repository.ManagementRepository
import fr.uha.hassenforder.easy.memorial.database.dao.GuideProgressDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate
import fr.uha.hassenforder.easy.memorial.dto.FullTourPerforming
import fr.uha.hassenforder.easy.memorial.model.GuideProgress
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourPerforming
import fr.uha.hassenforder.easy.memorial.model.ProgressState
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.dto.TourWithGuides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PerformingRepository(
    private val dispatcher: CoroutineDispatcher,
    private val tourProgressDao: TourProgressDao,
    private val guideProgressDao: GuideProgressDao,
    private val management: ManagementRepository<TourProgress, ManageableDao<TourProgress>>
) {

    fun getAll(): Flow<List<ManagedTourPerforming>> {
        return tourProgressDao.getAllPerforming()
    }

    fun getFiltered(): Flow<List<ManagedTourPerforming>> {
        return tourProgressDao.getFilteredPerforming()
    }

    fun getTourProgressById(tpid: Long): Flow<TourProgress?> {
        return tourProgressDao.getById(tpid)
    }

    fun getFullTourPerforming(progressId: Long): Flow<FullTourPerforming?> {
        return tourProgressDao.getFullTourPerformingById(progressId)
    }

    @WorkerThread
    suspend fun update(dto : TourProgressUpdate) = withContext(dispatcher) {
        when (dto) {
            is TourProgressUpdate.State -> tourProgressDao.update(dto)
            is TourProgressUpdate.Mode -> tourProgressDao.update(dto)
            is TourProgressUpdate.WithPicture -> tourProgressDao.update(dto)
            is TourProgressUpdate.WithHint -> tourProgressDao.update(dto)
            is TourProgressUpdate.WithDelay -> tourProgressDao.update(dto)
        }
    }

    @WorkerThread
    suspend fun createUsingTour(tourId : Long) : Long = withContext(dispatcher) {
        val tour: TourWithGuides = tourProgressDao.getSyncTourWithGuidesById(tourId)
        val progress = TourProgress(
            progressId = 0,
            tid = tourId,
            mode = tour.tour.mode,
            withPicture = tour.tour.withPicture,
            withHint = tour.tour.withHint,
            withDelay = tour.tour.withDelay,
            state = ProgressState.STARTED
        )
        val progressId : Long = create (progress)
        val guideProgresses = tour.guides.map {
            GuideProgress(
                gpId = 0,
                guideId = it.guideId,
                state = ProgressState.STARTED
            )
        }
        create (guideProgresses)
        return@withContext progressId
    }

    @WorkerThread
    suspend fun create(guideProgresses: List<GuideProgress>): List<Long> = withContext(dispatcher)  {
        return@withContext guideProgressDao.create(guideProgresses)
    }

    suspend fun updateGuideProgress(progress: GuideProgress) = withContext(dispatcher) {
        guideProgressDao.update(progress)
    }

    @WorkerThread
    suspend fun create(tourProgress: TourProgress): Long = management.create(tourProgress)

    @WorkerThread
    suspend fun valid(tid: Long, valid: Boolean) = management.valid (tid, valid)

    @WorkerThread
    suspend fun delete(tourProgress: TourProgress) = management.delete(tourProgress.progressId, tourProgress)

    @WorkerThread
    suspend fun edit(tid: Long): Long = management.edit(tid)

    @WorkerThread
    suspend fun restore(tid: Long): Boolean = management.restore(tid)

    @WorkerThread
    suspend fun hide(tid: Long): Boolean = management.hide(tid)

    @WorkerThread
    suspend fun show(tid: Long): Boolean = management.show(tid)

}
