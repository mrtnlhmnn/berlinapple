package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class LocationRepo: HashMap<String, Location>(), JSONConvertable {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    fun findLocationByString(locationStringFromProgram: String?): Location? {
        if (locationStringFromProgram == null) return null

        for (location in this.values.toList()) {
            if (   (locationStringFromProgram.equals(location.name, ignoreCase = true))
                || (locationStringFromProgram.startsWith(location.name, ignoreCase = true))
                || (location.name.startsWith(locationStringFromProgram))
            )
            {
                return Location(locationStringFromProgram, location.address, location.url)
            }
        }

        // fallback if not found
        LOGGER.warn("Could not find '${locationStringFromProgram}' in the location repo, hence using as fallback. " +
                "Please check location.json, might want to add '${locationStringFromProgram}' ...")

        return Location(name = locationStringFromProgram, address = null, url = null)
    }
}