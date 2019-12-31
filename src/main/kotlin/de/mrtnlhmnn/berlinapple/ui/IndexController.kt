package de.mrtnlhmnn.berlinapple.ui

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class IndexController {
    @RequestMapping("/")
    fun index(model: Model): String {
        return "index"
    }
}
