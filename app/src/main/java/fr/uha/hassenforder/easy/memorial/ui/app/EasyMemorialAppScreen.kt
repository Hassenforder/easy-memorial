package fr.uha.hassenforder.easy.memorial.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import fr.uha.hassenforder.easy.android.model.FlexibleIcon
import fr.uha.hassenforder.easy.android.model.IconRender
import fr.uha.hassenforder.easy.android.ui.app.AppNavigatorScreen
import fr.uha.hassenforder.easy.android.ui.app.PrimaryNavigation
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.navigation.GreetingDestination
import fr.uha.hassenforder.easy.memorial.navigation.PerformingDestination
import fr.uha.hassenforder.easy.memorial.navigation.SettingsDestination
import fr.uha.hassenforder.easy.memorial.navigation.TourDestination

@Composable
fun MemorialAppScreen(
    vm: AppViewModel = hiltViewModel(),
) {
    AppNavigatorScreen(vm.navigator, primaryNavigations)
}

private val primaryNavigations = listOf (
    PrimaryNavigation(
        destination = GreetingDestination.GreetingScreen,
        icon = IconRender(
            focused = FlexibleIcon(vector = Icons.Filled.Home),
            unfocused = FlexibleIcon(vector = Icons.Outlined.Home)
        ),
        labelId = R.string.app_name
    ),
    PrimaryNavigation(
        destination = TourDestination.ListScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Explore), unfocused = FlexibleIcon(vector= Icons.Outlined.Explore)),
        labelId = R.string.action_tours
    ),
    PrimaryNavigation(
        destination = PerformingDestination.ListScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Map), unfocused = FlexibleIcon(vector= Icons.Outlined.Map)),
        labelId = R.string.action_progress
    ),
    PrimaryNavigation(
        destination = SettingsDestination.SettingsScreen,
        icon = IconRender(focused = FlexibleIcon(vector= Icons.Filled.Settings), unfocused = FlexibleIcon(vector= Icons.Outlined.Settings)),
        labelId = R.string.action_settings
    ),
)
