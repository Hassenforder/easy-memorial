package fr.uha.hassenforder.easy.memorial.dto

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import fr.uha.hassenforder.easy.android.database.model.Management
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress

/*
 * Tour Performing join together a tour with its progress with its management if it exists
 * key is the tour as the progress can be null
 * two managements can exist, one for the tour and a second for the progress.
 * but only valid tour can be performed so management here is for progress
 * the SQL query should be well formed to retrieve the given fields
 */
@Immutable
class ManagedTourPerforming (
    @Embedded
    val tour: Tour,

    @Embedded
    val progress: TourProgress?,

    @Embedded
    val management: Management?
)