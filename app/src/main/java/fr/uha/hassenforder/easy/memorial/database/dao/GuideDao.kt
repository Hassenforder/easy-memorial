package fr.uha.hassenforder.easy.memorial.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.model.Guide
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
abstract class GuideDao : SmartDao<Guide>() {

    sealed class GuideUpdate (val guideUpdatedDate : Instant = Instant.now()) {
        data class Position(val guideId : Long, val position : Int) : GuideUpdate()
    }

    @Transaction
    @Query("SELECT * FROM guides WHERE guideId = :id")
    abstract fun getFullGuideById(id: Long?): Flow<FullGuide?>

    @Query("SELECT * FROM guides WHERE tourId = :tourId")
    abstract fun getAllByTourId(tourId: Long): List<Guide>

    @Update (entity = Guide::class)
    abstract fun update (content : GuideUpdate.Position)

    @Update (entity = Guide::class)
    abstract fun update (content : List<GuideUpdate.Position>)

}
