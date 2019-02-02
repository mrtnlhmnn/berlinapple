package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.time.Duration
import java.time.Period
import java.time.ZonedDateTime

@Controller
class BookingController(val movieRepo: MovieRepo) {

    @RequestMapping("/bookableMovies")
    fun listBookableMovies(model: Model): String {
        val movies = movieRepo.getSortedMovies()

        var bookableMovies = mutableListOf<Movie>()
        for (movie in movies) {
            if (movie.available) bookableMovies.add(movie)
        }
        model.addAttribute("movies", bookableMovies)

        return "bookableMovieList"
    }

    @GetMapping("/movie/bookEvent")
    fun bookEvent() = "/movielist"

    @PostMapping("/movie/bookEvent")
    fun bookEvent(@RequestParam("movieId") movieId: String,
             @RequestParam("eventId") eventId: String): RedirectView {
        val eventID = ID(eventId)
        val movieID = ID(movieId)

        // fix the event status and all events of its movie
        var bookedEvent: Event? = null
        val bookedMovie = movieRepo.get(movieID)

        bookedMovie.let {
            val events = it!!.events
            events.forEach { ev ->
                if (ev.id == eventID) {
                    bookedEvent = ev
                    bookedEvent?.status = EventStatus.BOOKED
                }
                else
                    ev.status = EventStatus.UNAVAILABLE
            }
        }

        // fix all other events of all other movies (especially set all events to unavailable,
        // if they intersect with the booked event)
        val movies = movieRepo.getSortedMovies()
        movies.forEach {
            if (it != bookedMovie) {
                val events = it!!.events
                events.forEach { ev ->
                    if (ev overlaps bookedEvent)
                        ev.status = EventStatus.UNAVAILABLE
                }
            }
        }

        return RedirectView("/bookableMovies")
    }
}