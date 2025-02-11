package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import de.mrtnlhmnn.berlinapple.data.*
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

    private val berlinaleStartDateAsLDT = LocalDateTime.parse(config.berlinaleStartDateTime, dateTimePatternFormatter)
    private val berlinaleEndDateAsLDT   = LocalDateTime.parse(config.berlinaleEndDateTime,   dateTimePatternFormatter)

    var eventsTotalCounter = 0;
    var eventsFilteredByTimeCounter = 0;

    val defaultURL = "https://www.berlinale.de"

    // ----------------------------------------------------
    private val beginKey       = Property.DTSTART
    private val endKey         = Property.DTEND
    private val locationKey    = Property.LOCATION
    private val summaryKey     = Property.SUMMARY
    private val descriptionKey = Property.DESCRIPTION
    private val urlKey         = Property.URL

//TODO break up this method, make it testable - extract the filter stuff out; check if the event numbers are correct
//do not pass in the repos but ask for the result to make this method testable
    fun parseProgramICSFile2Repo() {
        var fis: InputStream? = null
        try {
            fis = ProgramParser::class.java.classLoader.getResourceAsStream(config.programICSFileName)

            val builder = CalendarBuilder()
            val calendar = builder.build(fis)

            // get all data from parsed calendar ics file
            for (calEntry in calendar.components) {
                //TODO put these parameters to a separate class
                var beginZDTFromProgram: ZonedDateTime? = null
                var endZDTFromProgram: ZonedDateTime? = null
                var endStringFromProgram: String
                var locationStringFromProgram: String? = null
                var summaryStringFromProgram = ""
                var descriptionStringFromProgram = ""
                var urlFromProgram = URL(defaultURL)
                var lengthFromProgram: Long? = null


                // get next data from parsed calendar ics file
                for (calEntryProps in calEntry.properties) {
//TODO check if any of these values is still null in the end (might be ok for some , like URL but not for time values)
//TODO migrate to switch expression
                    when {
                        (calEntryProps.name == beginKey) -> {
                            beginZDTFromProgram = ZonedDateTime.parse(calEntryProps.value + "Z", dateTimePatternFormatterCal)
                                .minusHours(1) // set to German timezone
                        }
                        (calEntryProps.name == endKey) -> {
                            endStringFromProgram = calEntryProps.value
                            endZDTFromProgram = ZonedDateTime.parse(endStringFromProgram + "Z", dateTimePatternFormatterCal)
                                .minusHours(1) // set to German timezone
                        }
                        (calEntryProps.name == locationKey) -> {
                            locationStringFromProgram = calEntryProps.value
                        }
                        (calEntryProps.name == summaryKey) -> {
                            summaryStringFromProgram = calEntryProps.value
                        }
                        (calEntryProps.name == descriptionKey) -> {
                            descriptionStringFromProgram = calEntryProps.value

                            val lengthFromProgramRegex = """(\d+)(?=\s+Min)""".toRegex() // regexp to find occurences with "... Min" where (?= Min) is a Lookahead to only read the number before " Min".
                            val lengthMatch = lengthFromProgramRegex.findAll(calEntryProps.value)
                            lengthFromProgram = lengthMatch?.map { it.value.toInt() }.sum().toLong()
                        }
                        (calEntryProps.name == urlKey) -> {
                            if (calEntryProps.value != null && calEntryProps.value.isNotEmpty())
                                urlFromProgram = URL(calEntryProps.value)
                        }
                    }
                }

                eventsTotalCounter++

                // correct end time, if length could be found and is > 0
                if (lengthFromProgram != null && lengthFromProgram > 0) {
                    endZDTFromProgram = beginZDTFromProgram!!.plusMinutes(lengthFromProgram)
                }

                // only use data which has start/end time and a location...
                if (beginZDTFromProgram != null && endZDTFromProgram != null && locationStringFromProgram != null
                    // ... and only use events which are not "pressetermine"
                    // Reason: Some events in the ICAL/ICS file have a URL like
                    //     https://www.berlinale.de/de/presse/pressetermine/pressevorfuehrungen/detail.html?filmId=202504921
                    // which is not what we are interested in
                    // Note that the URL in the description text is even different from this URL. WTF
                    && !urlFromProgram.toString().contains("pressetermine")
                ) {
                    // only relevant events are used, filtered by time
                    if (isInStartEndTimeframe(beginZDTFromProgram, endZDTFromProgram, summaryStringFromProgram)) {
                        eventsFilteredByTimeCounter++

                        // Create Event from parsed calendar data
                        val location = locationRepo.findLocationByString(locationStringFromProgram)
                        val event = Event(beginZDTFromProgram, endZDTFromProgram, location)

                        // Find (or create new) Movie and attach the above Event to it
                        var movie = movieRepo.findByTitleIgnoreCase(summaryStringFromProgram)
                        if (movie == null) {
                            val prio = if (descriptionStringFromProgram.contains("Nur für Akkreditierte")) {
                                Prio.NURFUERAKKREDITIERTE
                            } else {
                                Prio.NORMAL
                            }

                            val newMovie = Movie(summaryStringFromProgram, descriptionStringFromProgram, prio, urlFromProgram)
                            movieRepo.addOrUpdate(newMovie)
                            movie = newMovie
                        }
                        movie.events.add(event)
                    }
                }
            }

            LOGGER.info("In total: {} events, left after date filter (between {} and {}) are now {} events",
                    eventsTotalCounter, berlinaleStartDateAsLDT, berlinaleEndDateAsLDT, eventsFilteredByTimeCounter)
        }
        finally {
            //TODO use try-with-resources, see https://www.baeldung.com/kotlin/try-with-resources
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
