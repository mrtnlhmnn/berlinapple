package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.DayRepo
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceScheduler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class PersistenceToggleController(val dayRepo: DayRepo, val persistenceScheduler: PersistenceScheduler) {

    @RequestMapping("/persistence/{toggle}")
    fun togglePersistence(@PathVariable("toggle") toggle: String, model: Model): String {
        var toggleVal = false
        if (toggle == "on") toggleVal = true
        persistenceScheduler.togglePersistence(toggleVal)

        model.addAttribute("toggle", toggleVal)
        model.addAttribute("days", dayRepo.getDaysAsStrings())
        return "persistenceToggle"
    }
}