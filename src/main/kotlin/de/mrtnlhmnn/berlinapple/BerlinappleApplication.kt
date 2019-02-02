package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo()
}

