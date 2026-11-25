package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.memorial.model.Tour

@Immutable
data class ManagedTour (
    @Embedded
    var tour: Tour,

    @Embedded
    var management: Management
)
