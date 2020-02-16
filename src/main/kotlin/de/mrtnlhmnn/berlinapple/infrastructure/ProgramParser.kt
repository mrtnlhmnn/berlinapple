package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.springframework.stereotype.Component
import java.net.URL
import java.time.format.DateTimeFormatter

import org.slf4j.LoggerFactory
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Component
class ProgramParser(val movieRepo: MovieRepo, val locationRepo: LocationRepo, val config: BerlinappleConfig) {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    // For Calendar entries
    private val dateTimePatternCal = "yyyyMMdd'T'HHmmssX"
    private val dateTimePatternFormatterCal = DateTimeFormatter.ofPattern(dateTimePatternCal)

    // For begin / end of Berlinale Time Frame
    private val dateTimePattern = "yyyyMMdd'T'HHmm"
    private val dateTimePatternFormatter = DateTimeFormatter.ofPattern(dateTimePattern)

    val berlinaleStartDateAsLDT = LocalDateTime.parse(config.berlinaleStartDateTime, dateTimePatternFormatter)
    val berlinaleEndDateAsLDT   = LocalDateTime.parse(config.berlinaleEndDateTime,   dateTimePatternFormatter)


    // ----------------------------------------------------
    private val beginKey       = Property.DTSTART
    private val endKey         = Property.DTEND
    private val locationKey    = Property.LOCATION
    private val summaryKey     = Property.SUMMARY
    private val descriptionKey = Property.DESCRIPTION
    private val urlKey         = Property.URL

    fun parseProgramICSFile2Repo() {
        var fis: InputStream? = null
        try {
            fis = ProgramParser::class.java.classLoader.getResourceAsStream(config.programICSFileName)

            val builder = CalendarBuilder()
            val calendar = builder.build(fis)

            var eventTotalCounter = 0;
            var eventNotFilteredCounter = 0;

            for (calEntry in calendar.getComponents()) {
                var beginZDTFromProgram: ZonedDateTime? = null
                var endZDTFromProgram: ZonedDateTime? = null
                var locationStringFromProgram: String? = null
                var summaryStringFromProgram = ""
                var descriptionStringFromProgram = ""
                var urlFromProgram: URL? = null

                // get data from parsed calendar ics file
                for (calEntryProps in calEntry.getProperties()) {
                    when {
                        (calEntryProps.name == beginKey) -> {
                            beginZDTFromProgram = ZonedDateTime.parse(calEntryProps.value, dateTimePatternFormatterCal)
                        }
                        (calEntryProps.name == endKey) -> {
                            endZDTFromProgram = ZonedDateTime.parse(calEntryProps.value, dateTimePatternFormatterCal)
                        }
                        (calEntryProps.name == locationKey) -> {
                            locationStringFromProgram = calEntryProps.value
                        }
                        (calEntryProps.name == summaryKey) -> {
                            summaryStringFromProgram = calEntryProps.value
                        }
                        (calEntryProps.name == descriptionKey) -> {
                            descriptionStringFromProgram = calEntryProps.value
                        }
                        (calEntryProps.name == urlKey) -> {
                            urlFromProgram = URL(calEntryProps.value); }
                    }
                }

                eventTotalCounter++

                if (beginZDTFromProgram != null && endZDTFromProgram != null && locationStringFromProgram != null) {
                    // only relevant events are used
                    if (isInStartEndTimeframe(beginZDTFromProgram, endZDTFromProgram, summaryStringFromProgram)) {
                        eventNotFilteredCounter++

                        // Create Event from parsed calendar data
                        val location = locationRepo.findLocationByString(locationStringFromProgram)
                        val event = Event(beginZDTFromProgram, endZDTFromProgram, location)

                        // try to find the category in the description
                        val categoryFromProgram = MovieCategory.findCategory(descriptionStringFromProgram)

                        // Find (or create new) Movie and attach the above Event to it
                        var movie = movieRepo.findByTitleIgnoreCase(summaryStringFromProgram)
                        if (movie == null) {
                            val newMovie = Movie(summaryStringFromProgram, descriptionStringFromProgram,
                                    Prio.NORMAL, urlFromProgram, categoryFromProgram)
                            movieRepo.addOrUpdate(newMovie)
                            movie = newMovie
                        }
                        movie.events.add(event)
                    }
                }
            }

            LOGGER.info("In total: {} events, left after date filter (between {} and {}) are now {} events",
                    eventTotalCounter, berlinaleStartDateAsLDT, berlinaleEndDateAsLDT, eventNotFilteredCounter)
        }
        finally {
            fis?.close();
        }
    }

    // check if event is in time frame [ berlinaleStartDateAsLD, berlinaleEndDateAsLD ]
    private fun isInStartEndTimeframe(eventBegin: ZonedDateTime, eventEnd: ZonedDateTime, eventSummary: String?): Boolean {
        val result = eventBegin.toLocalDateTime().isAfter(berlinaleStartDateAsLDT.minusHours(1))
                  && eventBegin.toLocalDateTime().isBefore(berlinaleEndDateAsLDT.plusHours(1))
                  && eventEnd.toLocalDateTime().isAfter(berlinaleStartDateAsLDT.minusHours(1))
                  && eventEnd.toLocalDateTime().isBefore(berlinaleEndDateAsLDT.plusHours(1))

        if (!result) {
            LOGGER.debug("Filtered out {} event with start={} and end={}", eventSummary, eventBegin, eventEnd)
        }

        return result
    }
}
