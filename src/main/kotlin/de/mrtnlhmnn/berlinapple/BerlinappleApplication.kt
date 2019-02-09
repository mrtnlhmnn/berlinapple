package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.application.listFromJSON
import de.mrtnlhmnn.berlinapple.application.toJSON
import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.URL
import java.time.ZonedDateTime

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)

    // parse location file
    context.getBean(LocationParser::class.java).parseLocationJSONFile2Repo()
    // parse program file
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo()
}

