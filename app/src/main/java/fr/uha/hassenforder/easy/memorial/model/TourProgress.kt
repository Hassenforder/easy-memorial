package fr.uha.hassenforder.easy.memorial.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable
import fr.uha.hassenforder.easy.android.database.model.Timable
import java.time.Instant

@Entity(tableName = "tourprogress")
@Immutable
data class TourProgress (
    @PrimaryKey(autoGenerate = true)
    var progressId : Long = 0,
    val tid : Long = 0,
    @ColumnInfo(name = "Pmode")
    val mode : TourMode,
    @ColumnInfo(name = "PwithPicture")
    val withPicture : WithHelper,
    @ColumnInfo(name = "PwithHint")
    val withHint : WithHelper,
    @ColumnInfo(name = "PwithDelay")
    val withDelay : WithHelper,
    @ColumnInfo(name = "Pstate")
    val state : ProgressState,
    var tpUpdatedDate: Instant? = null,
    var tpCreatedDate: Instant? = null,

    ) : Timable, Referencable {

    override fun getId(): Long {
        return progressId
    }

    override fun setId(id: Long) {
        progressId = id
    }

    override fun getCreatedDate(): Instant? {
        return tpCreatedDate
    }

    override fun setCreatedDate(date: Instant) {
        tpCreatedDate = date
    }

    override fun getUpdatedDate(): Instant? {
        return tpUpdatedDate
    }

    override fun setUpdatedDate(date: Instant) {
        tpUpdatedDate = date
    }

    fun needConfiguration () : Boolean {
        if (mode == TourMode.USER) return true
        if (withPicture == WithHelper.USER) return true
        if (withHint == WithHelper.USER) return true
        if (withDelay == WithHelper.USER) return true
        return false
    }

}
