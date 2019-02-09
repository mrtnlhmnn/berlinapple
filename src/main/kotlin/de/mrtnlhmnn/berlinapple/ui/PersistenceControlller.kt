package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.application.PersistenceScheduler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class PersistenceController(val persistenceScheduler: PersistenceScheduler) {

    @RequestMapping("/persistence/{toggle}")
    fun togglePersistence(@PathVariable("toggle") toggle: String, model: Model): String {
        var toggleVal = false
        if (toggle == "on") toggleVal = true
        persistenceScheduler.turnOnOffPersistence(toggleVal)

        model.addAttribute("toggle", toggleVal)
        return "persistenceToggle"
    }
}