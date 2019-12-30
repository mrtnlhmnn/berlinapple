package de.mrtnlhmnn.berlinapple.data

data class ID(var id: String) {

    companion object {
        private var eventCounter = 999L
        private val SEPARATOR = "-"

        fun createEventID(begin: String): ID {
            return ID("E" + SEPARATOR + begin + SEPARATOR + (++eventCounter).toString())
        }

        private fun hash(s: String) = String.format("%016X", Math.abs(s.hashCode()))

        fun createMovieID(s: String): ID {
            return ID("M" + SEPARATOR + hash(s))
        }
    }

    override fun toString(): String = "$id"
}
