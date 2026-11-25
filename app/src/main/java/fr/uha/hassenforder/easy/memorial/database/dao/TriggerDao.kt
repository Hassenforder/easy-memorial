package fr.uha.hassenforder.easy.memorial.database.dao

import androidx.room.Dao
import androidx.room.Update
import fr.uha.hassenforder.easy.android.database.dao.SmartDao
import fr.uha.hassenforder.easy.memorial.model.Trigger

@Dao
abstract class TriggerDao : SmartDao<Trigger>() {

    sealed class TriggerUpdate {
        data class Label(val triggerId : Long, val label: String) : TriggerUpdate()
        data class Latitude(val triggerId : Long, val latitude: String) : TriggerUpdate()
        data class Longitude(val triggerId : Long, val longitude: String) : TriggerUpdate()
        data class Hint(val triggerId : Long, val hint: String) : TriggerUpdate()
    }

    @Update (entity = Trigger::class)
    abstract suspend fun update (content : TriggerUpdate.Label)

    @Update (entity = Trigger::class)
    abstract suspend fun update (content : TriggerUpdate.Latitude)

    @Update (entity = Trigger::class)
    abstract suspend fun update (content : TriggerUpdate.Longitude)

    @Update (entity = Trigger::class)
    abstract suspend fun update (content : TriggerUpdate.Hint)

}
