package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable
import java.net.URL

data class Location (val name: String, val address: String?, val url: URL?): JSONConvertable {
    override fun toString(): String = "${name}"

    fun toStringWithAddress(): String {
        if (address !=null)
            return "${name} (${address})"
        else
            return toString()
    }
}
