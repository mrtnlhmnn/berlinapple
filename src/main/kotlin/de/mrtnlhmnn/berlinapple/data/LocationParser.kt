package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import de.mrtnlhmnn.berlinapple.application.listFromJSON
import de.mrtnlhmnn.berlinapple.application.toJSON
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component
import java.net.URL
import java.time.format.DateTimeFormatter

import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.io.InputStreamReader
import java.io.BufferedReader
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
