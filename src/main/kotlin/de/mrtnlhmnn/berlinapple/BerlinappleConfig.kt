package de.mrtnlhmnn.berlinapple

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class BerlinappleConfig {
    @Value("\${programFile:data/program.ics}")
    val programICSFileName: String = ""

    @Value("\${locationFile:data/locations.json}")
    val locationJSONFileName: String = ""

    @Value("\${berlinaleStartDateTime:20200221T1300}")
    val berlinaleStartDateTime: String = ""

    @Value("\${berlinaleEndDateTime:20200225T2000}")
    val berlinaleEndDateTime: String = ""

    @Value("\${build.version}")
    val buildVersion: String = ""

    @Value("\${build.date}")
    val buildDate: String = ""
}
