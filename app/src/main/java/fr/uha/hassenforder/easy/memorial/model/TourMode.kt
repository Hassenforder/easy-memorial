package fr.uha.hassenforder.easy.memorial.model

enum class TourMode {
    LOCKED_FREE,
    LOCKED_SEQUENCE,
    USER,
    USER_FREE,
    USER_SEQUENCE,
    ;

    fun isLocked(): Boolean {
        return TourMode.isLocked(this)
    }

    companion object {
        fun lockedChoices(): Array<TourMode> {
            return arrayOf(LOCKED_FREE, LOCKED_SEQUENCE, USER)
        }

        fun userChoices(): Array<TourMode> {
            return arrayOf(USER_FREE, USER_SEQUENCE)
        }

        fun isLocked(mode: TourMode?): Boolean {
            if (mode == null) return true
            if (mode == LOCKED_FREE) return true
            if (mode == LOCKED_SEQUENCE) return true
            return false
        }
    }

}
