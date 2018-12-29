package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

class TestMain

fun main(args: Array<String>) {
    println(ProgramParser().parseProgramICSFile())
}

