package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.Tour

/*
 * TourWithGuide join together a tour with its guides
 */
@Immutable
class TourWithGuides (
    @Embedded
    val tour: Tour,

    @Relation(parentColumn = "tourId", entityColumn = "tourId")
    val guides: List<Guide>

)