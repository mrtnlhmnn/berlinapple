package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceToS3Scheduler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class PersistenceToggleController(val persistenceScheduler: PersistenceToS3Scheduler) {

    @RequestMapping("/persistence/{toggle}")
    fun togglePersistence(@PathVariable("toggle") toggle: String, model: Model): String {
        var toggleVal = false
        if (toggle == "on") toggleVal = true
        persistenceScheduler.togglePersistence(toggleVal)

        model.addAttribute("toggle", toggleVal)
        return "persistenceToggle"
    }
}