package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.Prio
import org.apache.commons.lang3.math.NumberUtils.toInt
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class ProgramController(val movieRepo: MovieRepo) {
    @RequestMapping("/allMovies")
    fun listMovies(model: Model): String {
        model.addAttribute("movies", movieRepo.getSortedMovies())
        return "allMovies"
    }

    @RequestMapping("/movie/{id}")
    fun findMovie(@PathVariable("id") id: String, model: Model): String {
        model.addAttribute("movie", movieRepo.get(ID(id)))
        return "movie"
    }

    @GetMapping("/movie/changePrio")
    fun changePrio() = "/allMovies"

    @PostMapping("/movie/changePrio")
    fun changePrio(@RequestParam("id") id: String,
                   @RequestParam("prio") prio: String): RedirectView {
        return try {
            val movie = movieRepo.get(ID(id))
            val newPrio = toInt(prio)
            movie?.prio = Prio(newPrio)

            RedirectView("/allMovies")
        } catch (exception: Exception) {
            RedirectView("/")
        }
    }
}
