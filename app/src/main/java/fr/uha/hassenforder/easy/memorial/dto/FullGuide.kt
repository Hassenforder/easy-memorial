package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.android.ui.lazy.Orderable
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Trigger

@Immutable
data class FullGuide (
    @Embedded
    val guide : Guide,

    @Relation(parentColumn = "poiId", entityColumn = "poiId")
    val poi : POI,

    @Relation(parentColumn = "triggerId", entityColumn = "triggerId")
    val trigger: Trigger,

    ) : Orderable {

    override fun getOrder(): Int {
        return guide.position
    }

    override fun setOrder(order: Int) {
        guide.position = order
    }
}