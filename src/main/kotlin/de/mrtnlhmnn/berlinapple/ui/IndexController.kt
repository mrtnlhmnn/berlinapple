package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.BerlinappleConfig
import de.mrtnlhmnn.berlinapple.data.DayRepo
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController(val dayRepo: DayRepo, val config: BerlinappleConfig) {
    @RequestMapping("/")
    fun index(model: Model): String {
        model.addAttribute("buildVersion", config.buildVersion)
        model.addAttribute("buildDate",    config.buildDate)
        return "index"
    }
}
