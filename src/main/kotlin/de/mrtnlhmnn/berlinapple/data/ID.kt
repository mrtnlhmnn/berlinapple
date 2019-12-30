package de.mrtnlhmnn.berlinapple.data

data class ID(var id: String) {

    companion object {
        private var eventCounter = 999L
        private val SEPARATOR = "-"

        fun createEventID(begin: String) = ID("E" + SEPARATOR + begin + SEPARATOR + (++eventCounter).toString())

        private fun hashAndHex(s: String) = String.format("%016X", Math.abs(s.hashCode()))
        fun createMovieID(s: String) = ID("M" + SEPARATOR + hashAndHex(s))
    }

    override fun toString(): String = "$id"
}
