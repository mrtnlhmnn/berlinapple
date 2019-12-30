package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import de.mrtnlhmnn.berlinapple.data.Location
import de.mrtnlhmnn.berlinapple.data.LocationRepo
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

@Component
class LocationParser(val locationRepo: LocationRepo, val config: BerlinappleConfig) {

    fun parseLocationJSONFile2Repo() {
        val fis = LocationParser::class.java.classLoader.getResourceAsStream(config.locationJSONFileName)
        val locationFileAsString = IOUtils.toString(fis, StandardCharsets.UTF_8);
        val locationList = locationFileAsString.listFromJSON<Location>()

        if (locationList != null) {
            for (location in locationList) {
                locationRepo.put(location.name, location)
            }
        }
    }
}
