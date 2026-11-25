package fr.uha.hassenforder.easy.memorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.memorial.ui.tour.CreateTourScreen
import fr.uha.hassenforder.easy.memorial.ui.tour.EditTourScreen
import fr.uha.hassenforder.easy.memorial.ui.tour.ListToursScreen
import fr.uha.hassenforder.easy.memorial.ui.tour.guide.CreateGuideScreen
import fr.uha.hassenforder.easy.memorial.ui.tour.guide.EditGuideScreen
import kotlinx.serialization.Serializable

sealed interface TourDestination : NavKey {
    @Serializable
    data object ListScreen: NavKey

    @Serializable
    data object CreateScreen: NavKey

    @Serializable
    data class EditScreen(val tid: Long): NavKey

    @Serializable
    data class CreateGuideScreen (val tid: Long, val position: Int): NavKey

    @Serializable
    data class EditGuideScreen(val gid: Long): NavKey
}

fun EntryProviderScope<NavKey>.tourNavGraph () {
    entry<TourDestination.ListScreen> {
        ListToursScreen()
    }
    entry<TourDestination.CreateScreen> {
        CreateTourScreen()
    }
    entry<TourDestination.EditScreen> {
        EditTourScreen(tid = it.tid)
    }
    entry<TourDestination.CreateGuideScreen> {
        CreateGuideScreen(tourId = it.tid, position = it.position)
    }
    entry<TourDestination.EditGuideScreen> {
        EditGuideScreen(gid = it.gid)
    }
}