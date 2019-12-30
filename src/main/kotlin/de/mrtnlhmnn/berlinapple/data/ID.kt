package de.mrtnlhmnn.berlinapple.data

data class ID(var id: String) {

    companion object {
        private var eventCounter = 999L
        private val SEPARATOR = "-"

        fun createEventID(prefix: String) = ID("E" + SEPARATOR + prefix + SEPARATOR + (++eventCounter).toString())

        fun createMovieID(s: String) = ID("M" + SEPARATOR + s.hashAndHex())
    }

    override fun toString(): String = "$id"
}

private inline fun String.hashAndHex() = String.format("%016X", Math.abs(hashCode()))
