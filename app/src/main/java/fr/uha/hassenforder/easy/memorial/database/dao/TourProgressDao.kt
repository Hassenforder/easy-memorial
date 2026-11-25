package fr.uha.hassenforder.easy.memorial.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.memorial.dto.FullTourPerforming
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourPerforming
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.dto.TourWithGuides
import fr.uha.hassenforder.easy.memorial.model.ProgressState
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
abstract class TourProgressDao : SmartDao<TourProgress>() {

    sealed class TourProgressUpdate (val tpUpdatedDate : Instant = Instant.now()) {
        data class State(val progressId : Long, val Pstate : ProgressState?) : TourProgressUpdate()
        data class Mode(val progressId : Long, val Pmode : TourMode) : TourProgressUpdate()
        data class WithPicture(val progressId : Long, val PwithPicture : WithHelper) : TourProgressUpdate()
        data class WithHint(val progressId : Long, val PwithHint : WithHelper) : TourProgressUpdate()
        data class WithDelay(val progressId : Long, val PwithDelay : WithHelper) : TourProgressUpdate()
    }

    @Transaction
    @Query("SELECT T.*, P.*, XP.* " +
            "FROM tours AS T " +
            "INNER JOIN _managements AS XT " +
            "ON XT.refId = T.tourId AND XT.entity = 'Tour' " +
            "LEFT JOIN tourprogress AS P " +
            "ON P.tid = T.tourId " +
            "LEFT JOIN _managements AS XP " +
            "ON XP.refId = P.progressId AND XP.entity = 'TourProgress' " +
            "ORDER BY T.title "
    )
    abstract fun getAllPerforming(): Flow<List<ManagedTourPerforming>>

    @Transaction
    @Query("SELECT T.*, P.*, XP.* " +
            "FROM tours AS T " +
            "INNER JOIN _managements AS XT " +
            "ON XT.refId = T.tourId AND XT.entity = 'Tour' AND XT.state = 'VALID' " +
            "LEFT JOIN tourprogress AS P " +
            "ON P.tid = T.tourId " +
            "LEFT JOIN _managements AS XP " +
            "ON XP.refId = P.progressId AND XP.entity = 'TourProgress' AND ( XP.state = 'CREATED' OR XP.state = 'VALID' OR XP.state = 'INVALID' ) " +
            "ORDER BY T.title "
    )
    abstract fun getFilteredPerforming(): Flow<List<ManagedTourPerforming>>

    // warning synchronous operation must be called in a coroutine
    @Transaction
    @Query("SELECT * FROM tours WHERE tourId = :id")
    abstract fun getSyncTourWithGuidesById(id: Long): TourWithGuides

    @Transaction
    @Query("SELECT * FROM tourprogress WHERE progressId = :id")
    abstract fun getById(id: Long?): Flow<TourProgress?>

    @Transaction
    @Query("SELECT * FROM tourprogress WHERE progressId = :id")
    abstract fun getFullTourPerformingById(id: Long?): Flow<FullTourPerforming?>

    @Update(entity = TourProgress::class)
    abstract suspend fun update(content : TourProgressUpdate.State)

    @Update(entity = TourProgress::class)
    abstract suspend fun update(content : TourProgressUpdate.Mode)

    @Update(entity = TourProgress::class)
    abstract suspend fun update(content : TourProgressUpdate.WithPicture)

    @Update(entity = TourProgress::class)
    abstract suspend fun update(content : TourProgressUpdate.WithHint)

    @Update(entity = TourProgress::class)
    abstract suspend fun update(content : TourProgressUpdate.WithDelay)

    @Query("SELECT * FROM tourprogress WHERE progressId = :id")
    abstract fun getSyncById(id: Long): TourProgress?

    suspend fun clone (fromId : Long, toId : Long) : Long {
        val tourProgress : TourProgress? = getSyncById(fromId)
        if (tourProgress == null) return 0
        val cloneId : Long = upsert(tourProgress.copy(progressId = toId))
        return cloneId
    }

    /*
     * Strange but mandatory ...
     * implements the ManageableDao interface in the class create a bug
     * Room expect an annotation for all abstract fun even for fun not for it
     * So I chose to implement using a proxy
     */
    fun asManageable () : ManageableDao<TourProgress> {
        val self = this
        return object : ManageableDao<TourProgress> {
            override suspend fun getSync(id: Long): TourProgress? {
                return self.getSyncById(id)
            }
            override suspend fun create(entity: TourProgress): Long {
                return self.create(entity)
            }
            override suspend fun delete(entity: TourProgress) {
                return self.delete(entity)
            }
            override suspend fun clone(fromId: Long, toId: Long): Long {
                return self.clone(fromId, toId)
            }
            override fun getEntityName(): String {
                return TourProgress::class.simpleName!!
            }
        }
    }

}
