package de.mrtnlhmnn.berlinapple

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class BerlinappleConfig {
    @Value("\${programFile:data/program.ics}")
    val programICSFileName: String = ""

    @Value("\${locationFile:data/locations.txt}")
    val locationJSONFileName: String = ""

    @Value("\${berlinaleStartDateTime:20190207T1400}")
    val berlinaleStartDateTime: String = ""

    @Value("\${berlinaleEndDateTime:20190217T1800}")
    val berlinaleEndDateTime: String = ""
}
