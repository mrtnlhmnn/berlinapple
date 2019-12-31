package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.DayRepo
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceToS3Scheduler
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.Prio
import org.apache.commons.lang3.math.NumberUtils.toInt
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class MoviesController(val movieRepo: MovieRepo, val dayRepo: DayRepo, val persistenceScheduler: PersistenceToS3Scheduler) {
    @RequestMapping("/allMovies")
    fun allMovies(model: Model,
                  @RequestParam(required=false, name="filterDay") filterDay: String?): String {
        model.addAttribute("movies", movieRepo.getFilteredSortedMovies(filterDay))
        model.addAttribute("days", dayRepo.getDaysAsStrings())
        return "allMovies"
    }

    // ---------------------------------------------------------------------------------------------------

    @RequestMapping("/movie/{id}")
    fun getMovie(@PathVariable("id") id: String, model: Model): String {
        model.addAttribute("movie", movieRepo.get(id))
        model.addAttribute("days", dayRepo.getDaysAsStrings())
        return "movie"
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/movie/changePrio")
    fun changeMoviePrio() = "/allMovies"

    @PostMapping("/movie/changePrio")
    fun changeMoviePrio(@RequestParam("id") id: String,
                        @RequestParam("prio") prio: String): RedirectView {
        return try {
            val movie = movieRepo.get(id)
            val newPrio = toInt(prio)
            movie?.prio = Prio(newPrio)
            persistenceScheduler.changed()

            RedirectView("/allMovies")
        } catch (exception: Exception) {
            RedirectView("/")
        }
    }
}
