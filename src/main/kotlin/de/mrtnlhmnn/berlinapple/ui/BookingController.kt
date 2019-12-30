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
        val movies = movieRepo.getSortedMovies()

        var bookableMovies = mutableListOf<Movie>()
        for (movie in movies) {
            if (movie.available) bookableMovies.add(movie)
        }
        model.addAttribute("movies", bookableMovies)

        return "bookableMovies"
    }

    @GetMapping("/movie/bookEvent")
    fun bookEvent() = "/movielist"

    @PostMapping("/movie/bookEvent")
    fun bookEvent(@RequestParam("movieId") movieId: String,
             @RequestParam("eventId") eventId: String): RedirectView {
        val eventID = ID(eventId)
        val movieID = ID(movieId)

        // fix the event status and all events of its movie
        val bookedMovie = movieRepo.get(movieID)
        bookingHelper.bookEvent(bookedMovie, eventID)

        return RedirectView("/bookableMovies")
    }

    @GetMapping("/unbookMovie")
    fun unbookEvent() = "/bookedMovies"

    @PostMapping("/unbookMovie")
    fun unbookMovie(@RequestParam("movieId") movieId: String): RedirectView {
        // mark all events in unbooked movie as available
        val movieID = ID(movieId)
        val unbookedMovie = movieRepo.get(movieID)

        unbookedMovie.let {
            val events = it!!.events
            events.forEach { ev -> ev.status = EventStatus.AVAILABLE }
        }

        // reset all availabilities of all movies and at the same time collect list of booked movies and events...
        val movies = movieRepo.getSortedMovies()
        val bookedMoviesAndEvents: MutableList<Pair<Movie, Event>> = mutableListOf()

        movies.forEach {
            // reset availability - only for movies that are NOT booked! Events of booked movies must remain unavailable.
            if ( ! it.booked) {
                val events = it.events
                events.forEach { ev ->
                    if (ev.status == EventStatus.UNAVAILABLE)
                        ev.status = EventStatus.AVAILABLE
                }
            }
            // collect booked movies and events...
            if(it.booked) bookedMoviesAndEvents.add(Pair(it, it.getBookedEvent()))
        }

        // ... then fix them again them according to booked events
        bookedMoviesAndEvents.forEach {
            bookingHelper.fixAllEvents(it.first, it.second)
        }

        return RedirectView("/bookedMovies")
    }
}
