package de.mrtnlhmnn.berlinapple

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class BerlinappleConfig {
    @Value("\${programFile:data/program.ics}")
    val programICSFileName: String = ""

    @Value("\${locationFile:data/locations.json}")
    val locationJSONFileName: String = ""


    @Value("\${berlinaleStartDateTime:20240217T1200}")
//TODO Implement fallback if not set (or let the application crash at start time)
    val berlinaleStartDateTime: String = ""

    @Value("\${berlinaleEndDateTime:20240220T1900}")
//TODO Implement fallback if not set (or let the application crash at start time)
    val berlinaleEndDateTime: String = ""


    @Value("\${build.version}")
    val buildVersion: String = ""

    @Value("\${build.date}")
    val buildDate: String = ""
}
