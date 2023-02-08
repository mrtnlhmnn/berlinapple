package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProgramParserTest {

    @Autowired
    private lateinit var parser: ProgramParser

    @Test
    fun canReadProgramIcsFile() {
        // parser.parseProgramICSFile2Repo()
        assertThat(parser.eventTotalCounter).isEqualTo(1034)  // in 2023
        assertThat(parser.eventNotFilteredCounter).isEqualTo(295)  // in 2023
    }
}