package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BerlinappleApplication

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo("data/program.ics")
}

