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
        // in 2025 we have 890 movies/events in total, 340 in our filter time range

        assertThat(parser.eventsTotalCounter).isEqualTo(890)

        assertThat(parser.eventsFilteredByTimeCounter).isEqualTo(340)
        assertThat(movieRepo.getMovies().map { it.events.size }.sum()).isEqualTo(340)
    }
}