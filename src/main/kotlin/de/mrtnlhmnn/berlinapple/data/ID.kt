package de.mrtnlhmnn.berlinapple.data

data class ID(var id: String) {

    companion object {
        private val SEPARATOR = "-"

        fun createEventID(s: String) = ID("E" + SEPARATOR + s.hashAndHex())

        fun createMovieID(s: String) = ID("M" + SEPARATOR + s.hashAndHex())
    }

    override fun toString(): String = "$id"
}

private inline fun String.hashAndHex() = String.format("%016X", Math.abs(hashCode()))
