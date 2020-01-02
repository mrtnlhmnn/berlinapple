package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.Event
import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.infrastructure.LocationParser
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.ProgramParser
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceFromS3Reader
import org.springframework.stereotype.Component

@Component
class DataInitializer(
        private val locationParser: LocationParser,
        private val programParser: ProgramParser,
        private val persistenceFromS3Reader: PersistenceFromS3Reader,
        private val movieRepo: MovieRepo,
        private val bookingService: BookingService)
{
    init {
        // parse location file and fill the location repo
        locationParser.parseLocationJSONFile2Repo()

        // parse program file and fill the movie repo
        programParser.parseProgramICSFile2Repo()

        // read last movie list from S3
        val movieListFromS3 = persistenceFromS3Reader.getLastMovieListFromS3()

        // merge prios and event status from S3 (override the values from program)
        for (movieFromS3 in movieListFromS3) {
            movieRepo.get(movieFromS3)?.let { movieFromProgram ->
                movieFromProgram.prio = movieFromS3.prio

                // merge events and their state correctly
                for (eventInMovieFromProgram in movieFromProgram.events) {
                    val eventFromS3 = movieFromS3.events.findLast { it == eventInMovieFromProgram }
                    eventFromS3?.let { eventInMovieFromProgram.status = it.status }
                }

                if (movieFromProgram.booked || movieFromProgram.bookmarked) {
                    bookingService.fixStatusEvents(movieFromProgram, movieFromProgram.getBookedOrBookmarkedEvent()!!)
                }
            }
        }
    }
}
