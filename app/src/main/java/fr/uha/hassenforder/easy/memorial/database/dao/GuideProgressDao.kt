package fr.uha.hassenforder.easy.memorial.database.dao

import androidx.room.Dao
import androidx.room.Query
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.memorial.model.GuideProgress
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GuideProgressDao : SmartDao<GuideProgress>() {

    @Query("SELECT *  FROM guideprogress")
    abstract fun getAll(): Flow<List<GuideProgress>>

    @Query("SELECT * FROM guideprogress WHERE gpId = :id")
    abstract fun getById(id: Long?): Flow<GuideProgress?>

    @Query("DELETE FROM guideprogress WHERE guideId = :guideId")
    abstract fun deleteByGuideId(guideId: Long)

}
