package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo()
}

