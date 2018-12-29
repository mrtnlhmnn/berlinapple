package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.EventRepo
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

class TestMain

fun main(args: Array<String>) {
    println(ProgramParser(MovieRepo(), EventRepo()).parseProgramICSFile2Repo())
}

