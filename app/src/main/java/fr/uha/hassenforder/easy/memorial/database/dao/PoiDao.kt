package fr.uha.hassenforder.easy.memorial.database.dao

import android.net.Uri
import androidx.room.Dao
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.memorial.model.POI

@Dao
abstract class PoiDao : SmartDao<POI>() {

    sealed class PoiUpdate {
        data class Name(val poiId : Long, val name : String) : PoiUpdate()
        data class Description(val poiId : Long, val description: String) : PoiUpdate()
        data class Picture(val poiId : Long, val picture : Uri?) : PoiUpdate()
        data class Media(val poiId : Long, val media : Uri?) : PoiUpdate()
    }

    @Update (entity = POI::class)
    abstract suspend fun update (content : PoiUpdate.Name)

    @Update (entity = POI::class)
    abstract suspend fun update (content : PoiUpdate.Description)

    @Update (entity = POI::class)
    abstract suspend fun update (content : PoiUpdate.Picture)

    @Update (entity = POI::class)
    abstract suspend fun update (content : PoiUpdate.Media)

}
