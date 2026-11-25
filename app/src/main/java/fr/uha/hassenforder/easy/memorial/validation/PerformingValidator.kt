package fr.uha.hassenforder.easy.memorial.validation

import androidx.compose.runtime.Immutable
import fr.uha.hassenforder.easy.memorial.dto.FullTourPerforming
import fr.uha.hassenforder.easy.memorial.dto.GuidePerforming
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress

@Immutable
data class PerformingValidationError (
    val progress : Int?,
    val tour : Int?,
    val guides : Int?,
) {
    fun hasError () : Boolean {
        if (progress != null) return true
        if (tour != null) return true
        if (guides != null) return true
        return false
    }
}

class PerformingValidator (private val performing : FullTourPerforming) {

    private fun validateProgress(newValue: TourProgress?): Int? {
        return null
    }

    private fun validateTour(newValue: Tour?): Int? {
        return null
    }

    private fun validateGuides(newValue: List<GuidePerforming>): Int? {
        return null
    }

    fun validateAll () : PerformingValidationError {
        return PerformingValidationError(
            validateProgress(performing.progress),
            validateTour(performing.tour),
            validateGuides(performing.guides),
        )
    }

}