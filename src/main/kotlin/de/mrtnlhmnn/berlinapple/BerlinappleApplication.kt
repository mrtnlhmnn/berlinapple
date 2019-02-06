package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.URL
import java.time.ZonedDateTime

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo()
}

