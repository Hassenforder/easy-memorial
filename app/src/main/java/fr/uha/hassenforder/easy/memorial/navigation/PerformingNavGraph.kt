package fr.uha.hassenforder.easy.memorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import fr.uha.hassenforder.easy.memorial.ui.performing.ListPerformingScreen
import fr.uha.hassenforder.easy.memorial.ui.performing.perform.PerformPerformingScreen
import fr.uha.hassenforder.easy.memorial.ui.performing.progress.CreateProgressScreen
import kotlinx.serialization.Serializable

sealed interface PerformingDestination : NavKey {
    @Serializable
    data object ListScreen: NavKey

    @Serializable
    data class CreateProgressScreen(val tid : Long): NavKey

    @Serializable
    data class PerformProgressScreen(val pid: Long): NavKey

}

fun EntryProviderScope<NavKey>.performingNavGraph () {
    entry<PerformingDestination.ListScreen> {
        ListPerformingScreen()
    }
    entry<PerformingDestination.CreateProgressScreen> {
        CreateProgressScreen(tourId = it.tid)
    }
    entry<PerformingDestination.PerformProgressScreen> {
        PerformPerformingScreen(progressId = it.pid)
    }
}