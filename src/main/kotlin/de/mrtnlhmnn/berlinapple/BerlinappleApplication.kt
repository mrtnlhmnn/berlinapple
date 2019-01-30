package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BerlinappleApplication

@Value("programFile:data/program.ics")
val programICSFileName: String = "data/program.ics"

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo(programICSFileName)
}

