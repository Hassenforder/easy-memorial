package fr.uha.hassenforder.easy.memorial.model

enum class WithHelper {
    LOCKED_YES,
    LOCKED_NO,
    USER,
    USER_YES,
    USER_NO,
    ;
    fun isLocked(): Boolean {
        return WithHelper.isLocked(this)
    }

    companion object {
        fun lockedChoices(): Array<WithHelper> {
            return arrayOf(LOCKED_YES, LOCKED_NO, USER)
        }

        fun userChoices(): Array<WithHelper> {
            return arrayOf(USER_YES, USER_NO)
        }

        fun isLocked(with: WithHelper?): Boolean {
            if (with == null) return true
            if (with == LOCKED_YES) return true
            if (with == LOCKED_NO) return true
            return false
        }
    }

}
