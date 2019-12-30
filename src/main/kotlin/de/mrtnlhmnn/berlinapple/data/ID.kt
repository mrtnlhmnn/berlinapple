package de.mrtnlhmnn.berlinapple.data

data class ID(var id: String) {

    companion object {
        private var eventCounter = 999L
        private val SEPARATOR = "-"

        private fun hash(s: String): String {
            val hash = Math.abs(s.hashCode())
            val hashString = String.format("%016X", hash)
            return hashString
        }


        fun createEventID(begin: String): ID {
            return ID("E" + SEPARATOR + begin + SEPARATOR + (++eventCounter).toString())
        }
        fun createLegacyMovieID(): ID {
            return ID("M" + SEPARATOR + (++eventCounter).toString())
        }
        fun createMovieID(s: String): ID {
            return ID("M" + SEPARATOR + hash(s))
        }
    }

    override fun toString(): String = "$id"
}
