package fr.uha.hassenforder.easy.memorial.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable
import fr.uha.hassenforder.easy.android.database.model.Timable
import java.time.Instant

@Entity(tableName = "tours")
@Immutable
data class Tour (
    @PrimaryKey(autoGenerate = true)
    var tourId : Long = 0,
    val title : String,
    val description : String,
    val picture : Uri? = null,
    @ColumnInfo(name = "Tmode")
    val mode : TourMode,
    @ColumnInfo(name = "TwithPicture")
    val withPicture : WithHelper,
    @ColumnInfo(name = "TwithHint")
    val withHint : WithHelper,
    @ColumnInfo(name = "TwithDelay")
    val withDelay : WithHelper,
    var tourUpdatedDate: Instant? = null,
    var tourCreatedDate: Instant? = null,

    ) : Timable, Referencable {

    override fun getId(): Long {
        return tourId
    }

    override fun setId(id: Long) {
        tourId = id
    }

    override fun getCreatedDate(): Instant? {
        return tourCreatedDate
    }

    override fun setCreatedDate(date: Instant) {
        tourCreatedDate = date
    }

    override fun getUpdatedDate(): Instant? {
        return tourUpdatedDate
    }

    override fun setUpdatedDate(date: Instant) {
        tourUpdatedDate = date
    }

}
