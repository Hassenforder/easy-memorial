package fr.uha.hassenforder.easy.memorial.ui.performing.progress

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.group.Orientation
import fr.uha.hassenforder.easy.android.ui.field.group.OutlinedEnumRadioGroup
import fr.uha.hassenforder.easy.android.ui.field.group.RadioProperties
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper

@Composable
fun SuccessProgressScreen(
    uiState: ProgressViewModel.UIState,
    send : (ProgressViewModel.UIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (TourMode.isLocked(uiState.progress.mode)) {
            Text(text = stringResource(id = R.string.mode))
            Text(text = stringArrayResource(id = R.array.tourMode)[uiState.progress.mode.ordinal])
        } else {
            OutlinedEnumRadioGroup(
                value = uiState.progress.mode,
                onValueChange = { send(ProgressViewModel.UIEvent.ModeChanged(TourMode.valueOf(it))) },
                modifier = Modifier.fillMaxWidth(),
                properties = RadioProperties(
                    orientation = Orientation.HorizontalList,
                    itemValues = TourMode.userChoices(),
                    itemLabels = stringArrayResource(id = R.array.tourMode),
                ),
                decorator = DecoratorProperties(
                    labelId = R.string.mode,
                    errorId = uiState.validation.mode
                ),
            )
        }
        if (WithHelper.isLocked(uiState.progress.withPicture)) {
            Text(text = stringResource(id = R.string.with_picture))
            Text(text = stringArrayResource(id = R.array.withHelper)[uiState.progress.withPicture.ordinal])
        } else {
            OutlinedEnumRadioGroup(
                value = uiState.progress.withPicture,
                onValueChange = { send(
                    ProgressViewModel.UIEvent.WithPictureChanged(
                        WithHelper.valueOf(
                            it
                        )
                    )
                ) },
                properties = RadioProperties(
                    orientation = Orientation.HorizontalList,
                    itemValues = WithHelper.userChoices(),
                    itemLabels = stringArrayResource(id = R.array.withHelper),
                ),
                modifier = Modifier.fillMaxWidth(),
                decorator = DecoratorProperties(
                    labelId = R.string.with_picture,
                    errorId = uiState.validation.withPicture
                ),
            )
        }
        if (WithHelper.isLocked(uiState.progress.withHint)) {
            Text(text = stringResource(id = R.string.with_hint))
            Text(text = stringArrayResource(id = R.array.withHelper)[uiState.progress.withHint.ordinal])
        } else {
            OutlinedEnumRadioGroup(
                value = uiState.progress.withHint,
                onValueChange = { send(
                    ProgressViewModel.UIEvent.WithHintChanged(
                        WithHelper.valueOf(
                            it
                        )
                    )
                ) },
                properties = RadioProperties(
                    orientation = Orientation.HorizontalList,
                    itemValues = WithHelper.userChoices(),
                    itemLabels = stringArrayResource(id = R.array.withHelper),
                ),
                modifier = Modifier.fillMaxWidth(),
                decorator = DecoratorProperties(
                    labelId = R.string.with_hint,
                    errorId = uiState.validation.withHint
                ),
            )
        }
        if (WithHelper.isLocked(uiState.progress.withDelay)) {
            Text(text = stringResource(id = R.string.with_delay))
            Text(text = stringArrayResource(id = R.array.withHelper)[uiState.progress.withDelay.ordinal])
        } else {
            OutlinedEnumRadioGroup(
                value = uiState.progress.withDelay,
                onValueChange = { send(
                    ProgressViewModel.UIEvent.WithDelayChanged(
                        WithHelper.valueOf(
                            it
                        )
                    )
                ) },
                properties = RadioProperties(
                    orientation = Orientation.HorizontalList,
                    itemValues = WithHelper.userChoices(),
                    itemLabels = stringArrayResource(id = R.array.withHelper),
                ),
                modifier = Modifier.fillMaxWidth(),
                decorator = DecoratorProperties(
                    labelId = R.string.with_delay,
                    errorId = uiState.validation.withDelay
                ),
            )
        }
    }
}
