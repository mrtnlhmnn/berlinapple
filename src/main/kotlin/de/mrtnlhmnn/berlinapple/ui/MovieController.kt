package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.ProgramParser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class MovieController(val movieRepo: MovieRepo) {
    @RequestMapping("/movies")
    fun listMovies(model: Model): String {
        model.addAttribute("movies",
                movieRepo.values.toList().sortedByDescending { m -> m.prio })
        return "movies"
    }
}
