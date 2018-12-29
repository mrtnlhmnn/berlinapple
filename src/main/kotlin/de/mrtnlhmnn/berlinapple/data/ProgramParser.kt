package de.mrtnlhmnn.berlinapple.data

import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.model.Property
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class ProgramParser {
    val filename = "data/program.ics"

    val icsKeys = arrayOf(
            "DTSTART",
            "DTEND",
            "DTSTAMP",
            "UID",
            "CREATED",
            "DESCRIPTION",
            "LAST-MODIFIED",
            "LOCATION",
            "SEQUENCE",
            "STATUS",
            "SUMMARY",
            "TRANSP"
    )

    @RequestMapping(value = ["/parseProgramICSFile"], method = [RequestMethod.GET], produces = [MediaType.TEXT_PLAIN_VALUE])
    fun parseProgramICSFile(): String {
        val fis = ProgramParser::class.java.classLoader.getResourceAsStream(filename)
        val builder = CalendarBuilder()
        val calendar = builder.build(fis)

        var result = ""

        for (calEntry in calendar.getComponents()) {
            for (key in icsKeys) {
                for (calEntryProps in calEntry.getProperties<Property>(key)) {
                    result += calEntryProps.name + " = " + calEntryProps.value + "\n"
                }
            }
        }

        return result
    }
}
