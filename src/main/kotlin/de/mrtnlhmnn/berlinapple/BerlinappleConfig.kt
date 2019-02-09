package de.mrtnlhmnn.berlinapple

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class BerlinappleConfig {
    @Value("\${programFile:data/program.ics}")
    val programICSFileName: String = ""

    @Value("\${locationFile:data/locations.txt}")
    val locationJSONFileName: String = ""

    @Value("\${berlinaleStartDate:20190207}")
    val berlinaleStartDate: String = ""

    @Value("\${berlinaleEndDate:20190217}")
    val berlinaleEndDate: String = ""
}
