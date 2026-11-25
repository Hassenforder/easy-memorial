package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress

/*
 * Full Tour Performing joins together
 *  a tourprogress with its tour
 *  the list of guideprogress with its guide with trigger and poi
 */
@Immutable
class FullTourPerforming (
    @Embedded
    val progress: TourProgress,

    @Relation(parentColumn = "tid", entityColumn = "tourId")
    val tour: Tour,

    @Relation(parentColumn = "tid", entity= Guide::class, entityColumn = "tourId")
    val guides: List<GuidePerforming>,

    )