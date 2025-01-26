package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.infrastructure.LocationParser
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.infrastructure.ProgramParser
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceFromDiskReader
import de.mrtnlhmnn.berlinapple.application.BookingService
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val locationParser: LocationParser,
    private val programParser: ProgramParser,
    private val persistenceFromDiskReader: PersistenceFromDiskReader,
    private val movieRepo: MovieRepo,
    private val bookingService: BookingService)
{
    init {
        // parse location file and fill the location repo
        locationParser.parseLocationJSONFile2Repo()

        // parse program file and fill the movie repo
        programParser.parseProgramICSFile2Repo()

        // read last movie list from Disk

        val movieListFromDisk = persistenceFromDiskReader.getLastMovieListFromDisk()

        // merge prios and event status from Disk (override the values from program)
        for (movieFromDisk in movieListFromDisk) {
            movieRepo.get(movieFromDisk)?.let { movieFromProgram ->
                movieFromProgram.prio = movieFromDisk.prio

                // merge events and their state correctly
                for (eventInMovieFromProgram in movieFromProgram.events) {
                    val eventFromDisk = movieFromDisk.events.findLast { it == eventInMovieFromProgram }
                    eventFromDisk?.let { eventInMovieFromProgram.status = it.status }
                }

                if (movieFromProgram.booked || movieFromProgram.bookmarked) {
                    bookingService.fixStatusEvents(movieFromProgram, movieFromProgram.getBookedOrBookmarkedEvent()!!)
                }
            }
        }
    }
}
