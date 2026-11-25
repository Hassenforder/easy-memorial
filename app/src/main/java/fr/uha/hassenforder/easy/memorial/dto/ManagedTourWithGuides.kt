package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.Tour

@Immutable
data class ManagedTourWithGuides (
    @Embedded
    val tour: Tour,

    @Embedded
    var management: Management,

    @Relation(parentColumn = "tourId", entity = Guide::class, entityColumn = "tourId")
    val guides: List<FullGuide>,

    )