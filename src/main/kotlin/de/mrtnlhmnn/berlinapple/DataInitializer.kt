package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.LocationParser
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceFromS3Reader
import de.mrtnlhmnn.berlinapple.infrastructure.ProgramParser
import org.springframework.stereotype.Component

@Component
class DataInitializer(
        private val locationParser: LocationParser,
        private val programParser: ProgramParser,
        private val persistenceFromS3Reader: PersistenceFromS3Reader,
        private val movieRepo: MovieRepo)
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
//TODO If one changes the program.ics, the IDs for Movies and Events will change, so matching via ID will not work here
//TODO One could override the hashCode() from Movie and Event and NOT use the ID there!
            movieRepo.get(movieFromS3.id)?.let { movieFromProgram ->
                movieFromProgram.prio = movieFromS3.prio
                movieFromProgram.events.removeAll { true }
                movieFromProgram.events.addAll(movieFromS3.events) // brute-force replace from S3. not nice
            }
        }
    }
}

