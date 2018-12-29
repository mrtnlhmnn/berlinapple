package de.mrtnlhmnn.berlinapple.data

import net.fortuna.ical4j.data.CalendarBuilder
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
class ProgramParser(val movieRepo: MovieRepo, val eventRepo: EventRepo) {
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

    @RequestMapping(value = ["/listProgram"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listProgram(): HashMap<String, String?> {
        val myEventRepo: EventRepo = parseProgramICSFile2Repo()
        val map = HashMap<String, String?>()
        for (entry in myEventRepo.entries) {
            map.put(entry.key, entry.toString())
        }
//TODO: json geht nicht
//TODO: ID geht nicht in data class, wenn technischer Key aber besser abgeleitet aus fachlichem Schluessel
//TODO: UUID mit festem seed
        return map
    }

    fun parseProgramICSFile2Repo(): EventRepo {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(filename)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

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
            var movie = Movie(ID.create().toString(), summary , description)
            if (!movieRepo.containsKey(movie.title)) {
                movieRepo.put(movie.title, movie)
            }
            else {
                movie = movieRepo.get(movie.title)!!
            }

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
            var event = Event(ID.create().toString(), movie, begin, end, location)
            eventRepo.put("event"+event.id, event)
        }

        return eventRepo
    }
}
