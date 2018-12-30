package de.mrtnlhmnn.berlinapple.data

data class ID(val id: String) {

    companion object {
        private var counter = 9999L

        fun create(): ID {
            return ID((counter++).toString())
        }
    }

    override fun toString(): String = "$id"
}