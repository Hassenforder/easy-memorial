package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.android.ui.lazy.Orderable
import fr.uha.hassenforder.easy.memorial.model.GuideProgress

/*
 * Guide Performing join together a fullguide which is a guide with trigger and poi and its progress
 */
@Immutable
class GuidePerforming (
    @Embedded
    val guide: FullGuide,

    @Relation(parentColumn = "guideId", entityColumn = "guideId")
    val progress: GuideProgress,
) : Orderable {

    override fun getOrder(): Int {
        return guide.guide.position
    }

    override fun setOrder(order: Int) {
        guide.guide.position = order
    }
}