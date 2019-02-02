package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.springframework.stereotype.Component
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.slf4j.LoggerFactory
import java.time.LocalDate

@Component
class ProgramParser(val movieRepo: MovieRepo, val config: BerlinappleConfig) {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    private val dateTimePattern = "yyyyMMdd'T'HHmmss'Z'"
    private val dateTimePatternFormatter = DateTimeFormatter.ofPattern(dateTimePattern)
    private val datePattern     = "yyyyMMdd"
    private val datePatternFormatter = DateTimeFormatter.ofPattern(datePattern)

    val berlinaleStartDateAsLD = LocalDate.parse(config.berlinaleStartDate, datePatternFormatter)
    val berlinaleEndDateAsLD   = LocalDate.parse(config.berlinaleEndDate,   datePatternFormatter)


    // ----------------------------------------------------
    private val beginKey    = Property.DTSTART
    private val endKey      = Property.DTEND
    private val locationKey = Property.LOCATION
    private val sumKey      = Property.SUMMARY
    private val descKey     = Property.DESCRIPTION
    private val urlKey      = Property.URL

    fun parseProgramICSFile2Repo() {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(config.programICSFileName)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

        var eventTotalCounter = 0;
        var eventNotFilteredCounter = 0;

        for (calEntry in calendar.getComponents()) {
            var begin = LocalDateTime.MIN
            var end   = LocalDateTime.MIN

            var location = ""

            var summary = ""
            var description = ""
            var url: URL? = null

            // get data from parsed calendar ics file
            for (calEntryProps in calEntry.getProperties()) {
                when {
                    (calEntryProps.name == beginKey)    -> {begin    = LocalDateTime.parse(calEntryProps.value, dateTimePatternFormatter)}
                    (calEntryProps.name == endKey)      -> {end      = LocalDateTime.parse(calEntryProps.value, dateTimePatternFormatter)}
                    (calEntryProps.name == locationKey) -> {location = calEntryProps.value}
                    (calEntryProps.name == sumKey)      -> {summary      = calEntryProps.value }
                    (calEntryProps.name == descKey)     -> {description  = calEntryProps.value}
                    (calEntryProps.name == urlKey)      -> {url          = URL(calEntryProps.value); }
                }
            }


            eventTotalCounter++
            if (isInStartEndTimeframe(begin, end, summary)) {
                eventNotFilteredCounter++

                // Create Event from parsed calendar data
                val event = Event(ID.createEventID(begin.dayOfMonth.toString()), begin, end, location)

                // Find (or create new) Movie and attach the above Event to it
                var movie = movieRepo.findByTitleIgnoreCase(summary)
                if (movie == null) {
                    val movieId = ID.createMovieID()
                    movie = Movie(movieId, summary, description, 1, url, mutableListOf(event))
                    movieRepo.put(movieId, movie)
                } else {
                    movie.events?.add(event)
                }
            }
        }

        LOGGER.info("In total: {} events, left not filtered as between {} and {} are {} events",
                eventTotalCounter, berlinaleStartDateAsLD, berlinaleEndDateAsLD, eventNotFilteredCounter)
    }

    // check if event is in time frame [ berlinaleStartDateAsLD, berlinaleEndDateAsLD ]
    private fun isInStartEndTimeframe(eventBegin: LocalDateTime, eventEnd: LocalDateTime, eventSummary: String): Boolean {
        val result = eventBegin.toLocalDate().isAfter(berlinaleStartDateAsLD.minusDays(1))
                  && eventBegin.toLocalDate().isBefore(berlinaleEndDateAsLD.plusDays(1))
                  && eventEnd.toLocalDate().isAfter(berlinaleStartDateAsLD.minusDays(1))
                  && eventEnd.toLocalDate().isBefore(berlinaleEndDateAsLD.plusDays(1))

        if (!result) {
            LOGGER.debug("Filtered out {} event with start={} and end={}", eventSummary, eventBegin, eventEnd)
        }

        return result
    }
}
