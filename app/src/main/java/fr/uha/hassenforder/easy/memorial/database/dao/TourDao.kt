package fr.uha.hassenforder.easy.memorial.database.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.android.database.dao.ManageableDao
import fr.uha.hassenforder.easy.memorial.dto.ManagedTour
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourWithGuides
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
abstract class TourDao : SmartDao<Tour>() {

    sealed class TourUpdate (val tourUpdatedDate : Instant = Instant.now()) {
        data class Title(val tourId : Long, val title : String) : TourUpdate()
        data class Description(val tourId : Long, val description: String) : TourUpdate()
        data class Picture(val tourId : Long, val picture : Uri?) : TourUpdate()
        data class Mode(val tourId : Long, val Tmode : TourMode) : TourUpdate()
        data class WithPicture(val tourId : Long, val TwithPicture : WithHelper) : TourUpdate()
        data class WithHint(val tourId : Long, val TwithHint : WithHelper) : TourUpdate()
        data class WithDelay(val tourId : Long, val TwithDelay : WithHelper) : TourUpdate()
    }

    @Query("SELECT T.*, X.* " +
            "FROM tours AS T " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = T.tourId AND X.entity = 'Tour' " +
            "ORDER BY T.title "
    )
    abstract fun getAll(): Flow<List<ManagedTour>>

    @Query("SELECT T.*, X.* " +
            "FROM tours AS T " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = T.tourId AND X.entity = 'Tour' AND ( X.state = 'VALID' OR X.state = 'INVALID' ) " +
            "ORDER BY T.title "
    )
    abstract fun getFiltered(): Flow<List<ManagedTour>>

    @Transaction
    @Query("SELECT T.*, X.* " +
            "FROM tours AS T " +
            "INNER JOIN _managements AS X " +
            "ON X.refId = T.tourId AND X.entity = 'Tour' " +
            "WHERE T.tourId = :id "
    )
    abstract fun getTourWithGuides(id: Long?): Flow<ManagedTourWithGuides?>

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.Title)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.Description)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.Picture)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.Mode)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.WithPicture)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.WithHint)

    @Update(entity = Tour::class)
    abstract suspend fun update(content : TourUpdate.WithDelay)

    @Query("SELECT * FROM tours WHERE tourId = :id")
    abstract fun getSyncById(id: Long): Tour?

    suspend fun clone (fromId : Long, toId : Long) : Long {
        val tour : Tour? = getSyncById(fromId)
        if (tour == null) return 0
        val cloneId : Long = upsert(tour.copy(tourId=toId))
        return cloneId
    }

    /*
     * Strange but mandatory ...
     * implements the ManageableDao interface in the class create a bug
     * Room expect an annotation for all abstract fun even for fun not for it
     * So I chose to implement using a proxy
     */
    fun asManageable () : ManageableDao<Tour> {
        val self = this
        return object : ManageableDao<Tour> {
            override suspend fun getSync(id: Long): Tour? {
                return self.getSyncById(id)
            }
            override suspend fun create(entity: Tour): Long {
                return self.create(entity)
            }
            override suspend fun delete(entity: Tour) {
                return self.delete(entity)
            }
            override suspend fun clone(fromId: Long, toId: Long): Long {
                return self.clone(fromId, toId)
            }
            override fun getEntityName(): String {
                return Tour::class.simpleName!!
            }
        }
    }

}
