package fr.uha.hassenforder.easy.memorial.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun SettingsScreen(
    vm: SettingsViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(UITitle(screenNameId = R.string.title_settings)) },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)
        ) {
            StateScreen(uiState) {
                SuccessListScreen(it, { vm.send(it) })
            }
        }
    }

}

@Composable
private fun SuccessListScreen(
    fields: SettingsViewModel.UIState,
    send : (SettingsViewModel.UIEvent) -> Unit,
) {
    LazyColumn (
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            Button(onClick = { send(SettingsViewModel.UIEvent.OnClear) }) {
                Text(stringResource(id = R.string.clear))
            }
        }
        item {
            Button(onClick = { send(SettingsViewModel.UIEvent.OnPopulateByCode) }) {
                Text(stringResource(id = R.string.populate))
            }
        }
        items (fields.filenames) { item ->
            Button(onClick = { send(SettingsViewModel.UIEvent.OnPopulateByFile(item)) }) {
                Text(item.substringBefore('.'))
            }
        }
    }

}