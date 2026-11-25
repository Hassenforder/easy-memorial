package fr.uha.hassenforder.easy.memorial.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import fr.uha.hassenforder.easy.android.database.model.Referencable
import fr.uha.hassenforder.easy.android.database.model.Timable
import fr.uha.hassenforder.easy.android.ui.lazy.Orderable
import java.time.Instant

@Entity(
    tableName = "guides",
/*
    foreignKeys = [
        ForeignKey(
            entity = Tour::class,
            parentColumns = arrayOf("tourId"),
            childColumns = arrayOf("tourId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = POI::class,
            parentColumns = arrayOf("poiId"),
            childColumns = arrayOf("poiId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Trigger::class,
            parentColumns = arrayOf("triggerId"),
            childColumns = arrayOf("triggerId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
 */
)
@Immutable
data class Guide (
    @PrimaryKey(autoGenerate = true)
    var guideId : Long = 0,
    val tourId : Long = 0,
    val poiId : Long = 0,
    val triggerId : Long = 0,
    var position: Int = 0,
    var guideUpdatedDate: Instant? = null,
    var guideCreatedDate: Instant? = null,

) : Timable, Referencable, Orderable {

    override fun getId(): Long {
        return guideId
    }

    override fun setId(id: Long) {
        guideId = id
    }

    override fun getCreatedDate(): Instant? {
        return guideCreatedDate
    }

    override fun setCreatedDate(date: Instant) {
        guideCreatedDate = date
    }

    override fun getUpdatedDate(): Instant? {
        return guideUpdatedDate
    }

    override fun setUpdatedDate(date: Instant) {
        guideUpdatedDate = date
    }

    @Ignore
    override fun getOrder(): Int {
        return position
    }

    @Ignore
    override fun setOrder(order: Int) {
        this.position = order
    }

}