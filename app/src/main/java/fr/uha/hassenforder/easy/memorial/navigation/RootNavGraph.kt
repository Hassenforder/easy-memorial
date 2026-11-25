package fr.uha.hassenforder.easy.memorial.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.rootNavGraph () {
    greetingNavGraph()
    tourNavGraph()
    performingNavGraph()
    settingsNavGraph()
}