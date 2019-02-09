package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.application.JSONConvertable
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class LocationRepo: HashMap<String, Location>(), JSONConvertable {
    fun findLocationByString(locationStringFromProgram: String?): Location? {
        if (locationStringFromProgram == null) return null

        for (location in this.values.toList()) {
            // exact match?
            if (   (locationStringFromProgram.equals(location.name, ignoreCase = true))
                    || (locationStringFromProgram.indexOf(location.name, ignoreCase = true) != -1)
                    || (locationStringFromProgram.indexOf(getFirstWord(location.name), ignoreCase = true) != -1)
                    || (location.name.indexOf(locationStringFromProgram, ignoreCase = true) != -1)
                    || (location.name.indexOf(getFirstWord(locationStringFromProgram), ignoreCase = true) != -1)
            )
            {
                return Location(locationStringFromProgram, location.address, location.url)
            }
        }

        // fallback if not found
        return Location(name = locationStringFromProgram, address = null, url = null)
    }

    private fun getFirstWord(s: String): String {
        val p = Pattern.compile("[_a-zA-Z]+")
        val m = p.matcher(s)
        if (m.find()) {
            return m.group()
        }
        return s
    }
}