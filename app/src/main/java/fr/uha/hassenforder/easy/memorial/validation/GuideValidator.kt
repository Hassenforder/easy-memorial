package fr.uha.hassenforder.easy.memorial.validation

import android.net.Uri
import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.FullGuide

@Immutable
data class GuideValidationError (
    val position : Int?,
    val title : Int?,
    val description : Int?,
    val picture : Int?,
    val media : Int?,
    val label : Int?,
    val latitude : Int?,
    val longitude : Int?,
    val hint : Int?,
) {
    fun hasError () : Boolean {
        if (position != null) return true
        if (title != null) return true
        if (description != null) return true
        if (picture != null) return true
        if (media != null) return true
        if (label != null) return true
        if (latitude != null) return true
        if (longitude != null) return true
        if (hint != null) return true
        return false
    }
}

class GuideValidator (private val guide : FullGuide) {

    private fun validatePosition(newValue: Int) : Int? {
        return when {
            else -> null
        }
    }

    private fun validateTitle(newValue: String) : Int? {
        return when {
            newValue.isEmpty()  ->  R.string.value_empty
            newValue.isBlank()  ->  R.string.value_blank
            newValue.length < 2 ->  R.string.value_too_short
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
        if (newValue == null) return R.string.trigger_picture_is_mandatory
        return null
    }

    @Suppress("SameReturnValue", "unused")
    private fun validateMedia(newValue: Uri?) : Int? {
        return null
    }

    private fun validateLabel(newValue: String) : Int? {
        return when {
            newValue.isEmpty()  ->  R.string.value_empty
            newValue.isBlank()  ->  R.string.value_blank
            else -> null
        }
    }

    private fun validateLatitude(newValue: String) : Int? {
        return when {
            else -> null
        }
    }

    private fun validateLongitude(newValue: String) : Int? {
        return when {
            else -> null
        }
    }

    private fun validateHint(newValue: String) : Int? {
        return when {
            else -> null
        }
    }

    fun validateAll () : GuideValidationError {
        return GuideValidationError(
            validatePosition(guide.guide.position),
            validateTitle(guide.poi.name),
            validateDescription(guide.poi.description),
            validatePicture(guide.poi.picture),
            validateMedia(guide.poi.media),
            validateLabel(guide.trigger.label),
            validateLatitude(guide.trigger.latitude),
            validateLongitude(guide.trigger.longitude),
            validateHint(guide.trigger.hint),
        )
    }

}