package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.LocationRepo
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled
class ProgramParserTest {

    @Autowired
    lateinit var parser: ProgramParser

    @Autowired
    private lateinit var movieRepo: MovieRepo


    @Test
    fun canReadProgramIcsFile() {
        // in 2023 we have 1.034 movies/events in total, 295 in our filter time range

        assertThat(parser.eventTotalCounter).isEqualTo(1034)
        assertThat(parser.eventNotFilteredCounter).isEqualTo(295)

        assertThat(movieRepo.getMovies().map { it -> it.events.size }.sum()).isEqualTo(295)
    }
}