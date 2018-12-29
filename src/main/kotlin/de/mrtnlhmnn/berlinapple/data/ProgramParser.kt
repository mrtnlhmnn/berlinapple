package de.mrtnlhmnn.berlinapple.data

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.*
import java.time.format.DateTimeFormatter

@RestController
class ProgramParser {
    val filename = "data/program.ics"

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

    @RequestMapping(value = ["/parseProgramICSFile"], method = [RequestMethod.GET], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun parseProgramICSFile(): String {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(filename)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

        var result = ""

        for (calEntry in calendar.getComponents()) {
            // Create Movie
            val sumKey = "SUMMARY"
            val descKey = "DESCRIPTION"

            var summary: String = ""
            var description: String = ""
            for (calEntryProps in calEntry.getProperties()) {
                when {
                    (calEntryProps.name == sumKey)  -> {summary     = calEntryProps.value}
                    (calEntryProps.name == descKey) -> {description = calEntryProps.value}
                }
            }
            val movie = Movie(summary , description, ID.create().toString())

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
            val event = Event(movie, begin, end, location)

            result = result + event + "\n"
        }

        return result
    }
}
