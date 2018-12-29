package de.mrtnlhmnn.berlinapple.data

data class ID(val id: String) {
    companion object {
        val maxLength = 8

        fun create(): ID {
            val uuidAsLong = java.util.UUID.randomUUID().leastSignificantBits
            var uuidAsString = String.format("%016X", uuidAsLong)
            if (maxLength < uuidAsString.length) {
                uuidAsString = uuidAsString.substring(uuidAsString.length - maxLength, uuidAsString.length)
            }

            return ID(uuidAsString)
        }
    }
}