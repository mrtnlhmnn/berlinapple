package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import org.apache.commons.lang3.math.NumberUtils.toInt
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class MovieController(val movieRepo: MovieRepo) {
    @RequestMapping("/movielist")
    fun listMovies(model: Model): String {
        model.addAttribute("movies",
                movieRepo.values.toList().sortedByDescending { m -> m.prio })
        return "movielist"
    }

    @RequestMapping("/movie/{id}")
    fun findMovie(@PathVariable("id") id: String, model: Model): String {
        model.addAttribute("movie", movieRepo.get(ID(id)))
        return "movie"
    }

    @GetMapping("/movie/changePrio")
    fun changePrio() = "/movielist"

    @PostMapping("/movie/changePrio")
    fun changePrio(@RequestParam("id") id: String,
                   @RequestParam("prio") prio: String): RedirectView {
        return try {
            val movie = movieRepo.get(ID(id))
            val newPrio = toInt(prio)
            movie?.prio = newPrio

            RedirectView("/movielist")
        } catch (exception: Exception) {
            RedirectView("/")
        }
    }
}
