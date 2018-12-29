package de.mrtnlhmnn.berlinapple.ui

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class InfoController {
    @Value(value = "\${kapa.build.version:unknown}")
    private val buildVersion: String? = null

    @Value(value = "\${kapa.build.date:unknown}")
    // note that Maven generates its timestamp in UTC, see here:
    // https://stackoverflow.com/questions/28281988/how-to-have-maven-show-local-timezone-in-maven-build-timestamp
    private val buildDate: String? = null

    @RequestMapping(value = ["/version"], method = [RequestMethod.GET], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getBuildVersion(): HashMap<String, String?> =
            hashMapOf("buildVersion" to buildVersion, "buildDate" to buildDate)
}
