package de.mrtnlhmnn.berlinapple.data

data class ID(val id: String) {

    companion object {
        private var counter = 999L

        private val SEPARATOR = "-"

        fun createEventID(begin: String): ID {
            return ID("E" + SEPARATOR + begin + SEPARATOR + (++counter).toString())
        }
        fun createMovieID(): ID {
            return ID("M" + SEPARATOR + (++counter).toString())
        }
    }

    override fun toString(): String = "$id"
}
