package de.mrtnlhmnn.berlinapple.data

data class ID(val id: String) {

    companion object {
        private var eventCounter = 9999L
        private var movieCounter =   99L

        fun createEventID(): ID {
            return ID((++eventCounter).toString())
        }
        fun createMovieID(): ID {
            return ID((++movieCounter).toString())
        }
    }

    override fun toString(): String = "$id"
}