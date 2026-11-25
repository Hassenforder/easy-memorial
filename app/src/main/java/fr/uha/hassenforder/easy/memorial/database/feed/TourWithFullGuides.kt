package fr.uha.hassenforder.easy.memorial.database.feed

import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.Trigger

/*
 * These two classes are here just to help to feed the database
 * They gather all data in one place without id set explicitly
 * It is the feed behaviour to bind them correctly
 */
data class FullGuide (
    val guide : Guide,
    val poi : POI,
    val trigger: Trigger,
)

data class FullTour (
    val tour: Tour,
    val guides: List<FullGuide>
)
