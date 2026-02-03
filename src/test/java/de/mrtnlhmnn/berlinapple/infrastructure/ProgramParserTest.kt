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
        // in 2026 we have 933 movies/events in total, 349 in our filter time range

        assertThat(parser.eventsTotalCounter).isEqualTo(933)

        assertThat(parser.eventsFilteredByTimeCounter).isEqualTo(349)
        assertThat(movieRepo.getMovies().map { it.events.size }.sum()).isEqualTo(349)
    }
}