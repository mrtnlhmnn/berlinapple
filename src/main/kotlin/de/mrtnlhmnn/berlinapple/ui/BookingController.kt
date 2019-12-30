package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.application.BookingHelper
import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class BookingController(val movieRepo: MovieRepo, val bookingHelper: BookingHelper) {
    @RequestMapping("/bookableMovies")
    fun listBookableMovies(model: Model): String {
        var bookableMovies = mutableListOf<Movie>()
        for (movie in movieRepo.getSortedMovies()) {
            if (movie.available) bookableMovies.add(movie)
        }

        model.addAttribute("movies", bookableMovies)
        return "bookableMovies"
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/movie/bookEvent")
    fun bookEvent() = "/movielist"

    @PostMapping("/movie/bookEvent")
    fun bookEvent(@RequestParam("movieId") movieId: String,
                  @RequestParam("eventId") eventId: String): RedirectView {
        // fix the event status and all events of its movie
        val bookedMovie = movieRepo.get(ID(movieId))

        if (bookedMovie != null)
            bookingHelper.bookEvent(bookedMovie, ID(eventId))

        return RedirectView("/bookableMovies")
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/unbookMovie")
    fun unbookEvent() = "/bookedMovies"

    @PostMapping("/unbookMovie")
    fun unbookEvent(@RequestParam("movieId") movieId: String): RedirectView {
        // mark all events in unbooked movie as available
        val movieToUnbook = movieRepo.get(ID(movieId))

        if (movieToUnbook != null)
            bookingHelper.unbookEvent(movieToUnbook)

        return RedirectView("/bookedMovies")
    }
}
