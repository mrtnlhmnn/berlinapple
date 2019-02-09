package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.time.Instant

@RestController
class CalendarController {
    @Autowired
    private val movieRepo: MovieRepo? = null

    @RequestMapping(value = ["/calendar"], method = [RequestMethod.GET], produces = ["text/calendar"])
    fun downloadICSFile(@RequestParam("movieId") movieId: String): String {
        val movieID = ID(movieId)

        val bookedMovie = movieRepo?.get(movieID)
        val bookedEvent = bookedMovie?.getBookedEvent()
        val now = Instant.now().toString()

        if (bookedMovie != null && bookedEvent != null) {

//TODO: Description has linebreaks, hence not set here (need to be quoted)
            val icsText =
"""BEGIN:VCALENDAR
CALSCALE:GREGORIAN
METHOD:PUBLISH
PRODID:berlinapple
VERSION:2.0
X-WR-TIMEZONE:Europe/Berlin
X-WR-CALNAME:Berlinapple for the Internationale Filmfestspiele Berlin
X-WR-CALDESC:Berlinapple for the Internationale Filmfestspiele Berlin
BEGIN:VEVENT
UID:${movieID}-${bookedEvent.id}
DTSTAMP:${now}
CATEGORIES:Internationale Filmfestspiele Berlin
DTSTART:${bookedEvent.printBeginDateTime()}
DTEND:${bookedEvent.printEndDateTime()}
LOCATION:${bookedEvent.location?.let{"${it.name} (${it.address})"}}
SUMMARY:${bookedMovie.title}
URL:${bookedMovie.url}
END:VEVENT
END:VCALENDAR""".trimIndent()

            return icsText
        }
        return ""
    }
}
