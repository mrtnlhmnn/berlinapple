package de.mrtnlhmnn.berlinapple.data

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.springframework.stereotype.Component
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class ProgramParser(val movieRepo: MovieRepo) {
    private val beginKey    = Property.DTSTART
    private val endKey      = Property.DTEND
    private val locationKey = Property.LOCATION
    private val sumKey      = Property.SUMMARY
    private val descKey     = Property.DESCRIPTION
    private val urlKey      = Property.URL

    fun parseProgramICSFile2Repo(fileName: String) {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(fileName)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

        for (calEntry in calendar.getComponents()) {
            var begin = LocalDateTime.MIN
            var end   = LocalDateTime.MIN
            val pattern = "yyyyMMdd'T'HHmmss'Z'"
            val dtf = DateTimeFormatter.ofPattern(pattern)

            var location = ""

            var summary = ""
            var description = ""
            var url: URL? = null

            // get data from parsed calendar ics file
            for (calEntryProps in calEntry.getProperties()) {
                when {
                    (calEntryProps.name == beginKey)    -> {begin    = LocalDateTime.parse(calEntryProps.value, dtf)}
                    (calEntryProps.name == endKey)      -> {end      = LocalDateTime.parse(calEntryProps.value, dtf)}
                    (calEntryProps.name == locationKey) -> {location = calEntryProps.value}
                    (calEntryProps.name == sumKey)  -> {summary      = calEntryProps.value }
                    (calEntryProps.name == descKey) -> {description  = calEntryProps.value}
                    (calEntryProps.name == urlKey)  -> {url          = URL(calEntryProps.value); }
                }
            }

            // Create Event from parsed calendar data
            val event = Event(ID.create(), begin, end, location)

            // Find (or create new) Movie and attach the above Event to it
            var movie = movieRepo.findByTitleIgnoreCase(summary)
            if (movie == null) {
                val id = ID.create()
                movie = Movie(id, summary, description, 1, url, mutableListOf(event))
                movieRepo.put(id, movie)
            }
            else {
                movie.events?.add(event)
            }
        }
    }
}
