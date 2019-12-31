package de.mrtnlhmnn.berlinapple.ui

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController {
    @RequestMapping("/")
    fun index(model: Model): String {
//TODO hardcoded
        model.addAttribute("days", listOf("20190214", "20190215", "20190216", "20190217"))
        return "index"
    }
}
