package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.time.ZonedDateTime

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val event = Event( ID("11"), ZonedDateTime.now(), ZonedDateTime.now(), "here", EventStatus.BOOKED)
    val eventAsJson = event.toJSON()
    val parsedEvent = eventAsJson.fromJSON<Event>()
    println(event)
    println(eventAsJson)
    println(parsedEvent)
    println(parsedEvent == event)

    System.exit(0)

    val context = runApplication<BerlinappleApplication>(*args)
    context.getBean(ProgramParser::class.java).parseProgramICSFile2Repo()
}

