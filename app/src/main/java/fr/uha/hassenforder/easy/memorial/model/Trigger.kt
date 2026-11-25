package fr.uha.hassenforder.easy.memorial.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable

@Entity(
    tableName = "triggers",
)
@Immutable
data class Trigger (
    @PrimaryKey(autoGenerate = true)
    var triggerId : Long = 0,
    val label : String,
    val latitude : String,
    val longitude : String,
    val hint: String,
) : Referencable {

    override fun getId(): Long {
        return triggerId
    }

    override fun setId(id: Long) {
        triggerId = id
    }

}