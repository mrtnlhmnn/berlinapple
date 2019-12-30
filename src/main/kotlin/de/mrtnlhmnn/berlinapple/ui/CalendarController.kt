package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RestController
class CalendarController {
    @Autowired
    private val movieRepo: MovieRepo? = null

    @RequestMapping(value = ["/calendar"], method = [RequestMethod.GET], produces = ["text/calendar"])
    fun downloadICSFile(@RequestParam("movieId") movieId: String): String {
        val movieID = ID(movieId)

        val bookedMovie = movieRepo?.get(movieID)
        val bookedEvent = bookedMovie?.getBookedEvent()

        val now = Instant.now().atZone(ZoneId.of("UTC"))
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
        val nowAsString = now.format(formatter)

        if (bookedMovie != null && bookedEvent != null) {

            val icsText =
"""BEGIN:VCALENDAR
PRODID:BerlinAPPle
VERSION:2.0
CALSCALE:GREGORIAN
METHOD:PUBLISH
X-WR-CALDESC:BerlinAPPle for the Internationale Filmfestspiele Berlin
X-WR-CALNAME:BerlinAPPle for the Internationale Filmfestspiele Berlin
X-WR-TIMEZONE:Europe/Berlin
BEGIN:VEVENT
DTSTART:${bookedEvent.printBeginDateTimeForCalendarFile()}
DTEND:${bookedEvent.printEndDateTimeForCalendarFile()}
DTSTAMP:${nowAsString}
UID:${movieID}#${bookedEvent.id}
CREATED:${nowAsString}
DESCRIPTION:Berlinale, see ${bookedMovie.url}
LAST-MODIFIED:${nowAsString}
LOCATION:${bookedEvent.location?.let{"${it.name} (${it.address})"}}
SEQUENCE:0
STATUS:CONFIRMED
SUMMARY:Berlinale: ${bookedMovie.title}
TRANSP:OPAQUE
END:VEVENT
END:VCALENDAR""".trimIndent()

            return icsText
        }
        return ""
    }
}
