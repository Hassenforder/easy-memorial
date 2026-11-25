package fr.uha.hassenforder.easy.memorial.ui.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.group.Orientation
import fr.uha.hassenforder.easy.android.ui.field.group.OutlinedEnumRadioGroup
import fr.uha.hassenforder.easy.android.ui.field.group.RadioProperties
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper

@Composable
fun TourRunScreen(
    uiState: TourViewModel.UIState,
    send : (TourViewModel.UIEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedEnumRadioGroup(
            value = uiState.tour.tour.mode,
            onValueChange = { send(TourViewModel.UIEvent.ModeChanged(TourMode.valueOf(it))) },
            properties = RadioProperties(
                orientation = Orientation.HorizontalList,
                itemValues = TourMode.lockedChoices(),
                itemLabels = stringArrayResource(id = R.array.tourMode),
            ),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.mode,
                errorId = uiState.validation.mode
            ),
        )
        OutlinedEnumRadioGroup(
            value = uiState.tour.tour.withPicture,
            onValueChange = { send(TourViewModel.UIEvent.WithPictureChanged(WithHelper.valueOf(it))) },
            properties = RadioProperties(
                orientation = Orientation.HorizontalList,
                itemValues = WithHelper.lockedChoices(),
                itemLabels = stringArrayResource(id = R.array.withHelper),
            ),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.with_picture,
                errorId = uiState.validation.withPicture
            ),
        )
        OutlinedEnumRadioGroup(
            value = uiState.tour.tour.withHint,
            onValueChange = { send(TourViewModel.UIEvent.WithHintChanged(WithHelper.valueOf(it))) },
            properties = RadioProperties(
                orientation = Orientation.HorizontalList,
                itemValues = WithHelper.lockedChoices(),
                itemLabels = stringArrayResource(id = R.array.withHelper),
            ),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.with_hint,
                errorId = uiState.validation.withHint
            ),
        )
        OutlinedEnumRadioGroup(
            value = uiState.tour.tour.withDelay,
            onValueChange = { send(TourViewModel.UIEvent.WithDelayChanged(WithHelper.valueOf(it))) },
            properties = RadioProperties(
                orientation = Orientation.HorizontalList,
                itemValues = WithHelper.lockedChoices(),
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
