package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.MovieRepo
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
        // in 2024 we have 1.034 movies/events in total, 295 in our filter time range

        assertThat(parser.eventsTotalCounter).isEqualTo(239)

        assertThat(parser.eventsFilteredByTimeCounter).isEqualTo(239)
        assertThat(movieRepo.getMovies().map { it.events.size }.sum()).isEqualTo(239)
    }
}