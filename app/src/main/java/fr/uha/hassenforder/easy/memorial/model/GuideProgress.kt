package fr.uha.hassenforder.easy.memorial.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable
import fr.uha.hassenforder.easy.android.database.model.Timable
import java.time.Instant

@Entity(tableName = "guideprogress")
@Immutable
data class GuideProgress (
    @PrimaryKey(autoGenerate = true)
    var gpId : Long = 0,
    val guideId : Long = 0,
    val state : ProgressState,
    var gpUpdatedDate: Instant? = null,
    var gpCreatedDate: Instant? = null,

    ) : Timable, Referencable {

    override fun getId(): Long {
        return gpId
    }

    override fun setId(id: Long) {
        gpId = id
    }

    override fun getCreatedDate(): Instant? {
        return gpCreatedDate
    }

    override fun setCreatedDate(date: Instant) {
        gpCreatedDate = date
    }

    override fun getUpdatedDate(): Instant? {
        return gpUpdatedDate
    }

    override fun setUpdatedDate(date: Instant) {
        gpUpdatedDate = date
    }

}
