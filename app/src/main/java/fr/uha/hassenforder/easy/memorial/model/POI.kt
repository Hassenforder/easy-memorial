package fr.uha.hassenforder.easy.memorial.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable

@Entity(
    tableName = "pois",
)
@Immutable
data class POI (
    @PrimaryKey(autoGenerate = true)
    var poiId : Long = 0,
    val name : String,
    val description : String,
    val picture : Uri? = null,
    val media : Uri? = null,
) : Referencable {

    override fun getId(): Long {
        return poiId
    }

    override fun setId(id: Long) {
        poiId = id
    }

}