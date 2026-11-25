package fr.uha.hassenforder.easy.memorial.validation

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.model.ProgressState
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.model.WithHelper

@Immutable
data class ProgressValidationError (
    val mode : Int?,
    val withPicture : Int?,
    val withHint : Int?,
    val withDelay : Int?,
    val state : Int?,
) {
    fun hasError () : Boolean {
        if (mode != null) return true
        if (withPicture != null) return true
        if (withHint != null) return true
        if (withDelay != null) return true
        if (state != null) return true
        return false
    }
}

class ProgressValidator (private val progress: TourProgress) {

    private fun validateState(newValue: ProgressState?): Int? {
        return when {
            newValue == null ->  R.string.mode_must_set
            else -> null
        }
    }

    private fun validateMode(newValue: TourMode?): Int? {
        return when {
            newValue == null ->  R.string.mode_must_set
            newValue == TourMode.USER -> R.string.mode_must_set
            else -> null
        }
    }

    private fun validateWithPicture(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_picture_must_set
            newValue == WithHelper.USER -> R.string.with_picture_must_set
            else -> null
        }
    }

    private fun validateWithHint(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_hint_must_set
            newValue == WithHelper.USER -> R.string.with_hint_must_set
            else -> null
        }
    }

    private fun validateWithDelay(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_delay_must_set
            newValue == WithHelper.USER -> R.string.with_delay_must_set
            else -> null
        }
    }

    private fun validateNeedConfiguration(newValue: Boolean): Int? {
        if (! newValue) return R.string.progress_must_configured
        return null
    }

    fun validateAll () : ProgressValidationError {
        return ProgressValidationError(
            validateMode(progress.mode),
            validateWithPicture(progress.withPicture),
            validateWithHint(progress.withHint),
            validateWithDelay(progress.withDelay),
            validateState(progress.state),
        )
    }

}