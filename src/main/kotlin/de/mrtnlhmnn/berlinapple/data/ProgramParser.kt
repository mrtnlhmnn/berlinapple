package de.mrtnlhmnn.berlinapple.data

import net.fortuna.ical4j.data.CalendarBuilder
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class ProgramParser(val movieRepo: MovieRepo) {
    val icsKeys = arrayOf(
            "DTSTART",
            "DTEND",
            "DTSTAMP",
            "UID",
            "CREATED",
            "DESCRIPTION",
            "LAST-MODIFIED",
            "LOCATION",
            "SEQUENCE",
            "STATUS",
            "SUMMARY",
            "TRANSP"
    )

    fun parseProgramICSFile2Repo(fileName: String) {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(fileName)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

        for (calEntry in calendar.getComponents()) {
            // Create Event
            val beginKey = "DTSTART"
            val endKey = "DTEND"
            val locationKey = "LOCATION"

            var begin: LocalDateTime = LocalDateTime.MIN
            var end: LocalDateTime = LocalDateTime.MIN
            var location: String = ""

            val pattern = "yyyyMMdd'T'HHmmss'Z'"
            val dtf = DateTimeFormatter.ofPattern(pattern)

            for (calEntryProps in calEntry.getProperties()) {
                when {
                    (calEntryProps.name == beginKey)    -> {begin    = LocalDateTime.parse(calEntryProps.value, dtf)}
                    (calEntryProps.name == endKey)      -> {end      = LocalDateTime.parse(calEntryProps.value, dtf)}
                    (calEntryProps.name == locationKey) -> {location = calEntryProps.value}
                }
            }
            var event = Event(ID.create().toString(), begin, end, location)

            // -----------------------------------------------------------
            // Create Movie
            val sumKey = "SUMMARY"
            val descKey = "DESCRIPTION"

            var summary: String = ""
            var description: String = ""
            var movieKey: String = ""
            for (calEntryProps in calEntry.getProperties()) {
                when {
                    (calEntryProps.name == sumKey)  -> {summary     = calEntryProps.value; movieKey = summary.toLowerCase()}
                    (calEntryProps.name == descKey) -> {description = calEntryProps.value}
                }
            }

            if (!movieRepo.containsKey(movieKey)) {
                var movie = Movie(ID.create().toString(), summary, description, 1, mutableListOf(event))
                movieRepo.put(movieKey, movie)
            }
            else {
                val movie = movieRepo.get(movieKey)
                movie?.events?.add(event)
            }
        }
    }
}
