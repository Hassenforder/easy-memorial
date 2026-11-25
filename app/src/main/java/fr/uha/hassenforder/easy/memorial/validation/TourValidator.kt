package fr.uha.hassenforder.easy.memorial.validation

import android.net.Uri
import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourWithGuides
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper

@Immutable
data class TourValidationError (
    val title : Int?,
    val description : Int?,
    val picture : Int?,
    val mode : Int?,
    val withPicture : Int?,
    val withHint : Int?,
    val withDelay : Int?,
    val guides : Int?,
) {
    fun hasError () : Boolean {
        if (title != null) return true
        if (description != null) return true
        if (picture != null) return true
        if (mode != null) return true
        if (withPicture != null) return true
        if (withHint != null) return true
        if (withDelay != null) return true
        if (guides != null) return true
        return false
    }
}

class TourValidator (private val tour : ManagedTourWithGuides) {

    private fun validateTitle(newValue: String) : Int? {
        return when {
            newValue.isEmpty()  ->  R.string.value_empty
            newValue.isBlank()  ->  R.string.value_blank
            newValue.length < 3 ->  R.string.value_too_short
            newValue.length > 32 ->  R.string.value_too_long
            else -> null
        }
    }

    private fun validateDescription(newValue: String) : Int? {
        return when {
            newValue.isEmpty()  ->  R.string.value_empty
            newValue.isBlank()  ->  R.string.value_blank
/*
            newValue.length < 100 ->  R.string.value_too_short
            newValue.length > 500 ->  R.string.value_too_long
 */
            else -> null
        }
    }

    @Suppress("SameReturnValue", "unused")
    private fun validatePicture(newValue: Uri?) : Int? {
        return null
    }

    private fun validateMode(newValue: TourMode?): Int? {
        return when {
            newValue == null ->  R.string.mode_must_set
            else -> null
        }
    }

    private fun validateWithPicture(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_picture_must_set
            else -> null
        }
    }

    private fun validateWithHint(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_hint_must_set
            else -> null
        }
    }

    private fun validateWithDelay(newValue: WithHelper?): Int? {
        return when {
            newValue == null ->  R.string.with_delay_must_set
            else -> null
        }
    }

    private fun validateGuides(newValue: List<FullGuide>?) : Int? {
        if (newValue == null) return R.string.guides_not_empty
        val size = newValue.size
        return when {
//            size == 0 -> R.string.guides_not_empty
//            size < 3 ->  R.string.guides_not_enough
//            size > 6 ->  R.string.guides_too_much
            else -> null
        }
    }

    fun validateAll () : TourValidationError {
        return TourValidationError(
            validateTitle(tour.tour.title),
            validateDescription(tour.tour.description),
            validatePicture(tour.tour.picture),
            validateMode(tour.tour.mode),
            validateWithPicture(tour.tour.withPicture),
            validateWithHint(tour.tour.withHint),
            validateWithDelay(tour.tour.withDelay),
            validateGuides(tour.guides),
        )
    }
}