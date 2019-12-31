package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.application.BookingService
import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class BookingController(val movieRepo: MovieRepo, val dayRepo: DayRepo, val bookingService: BookingService) {
    @RequestMapping("/bookableMovies")
    fun listBookableMovies(model: Model,
                           @RequestParam(required=false, name="filterDay") filterDay: String?): String {
        val bookableMovies = movieRepo.getFilteredSortedMovies(filterDay).filter { it.available }
        model.addAttribute("movies", bookableMovies)
        model.addAttribute("days", dayRepo.getDaysAsStrings())
        return "bookableMovies"
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/movie/bookEvent")
    fun bookEvent() = "/movielist"

    @PostMapping("/movie/bookEvent")
    fun bookEvent(@RequestParam("movieId") movieId: String,
                  @RequestParam("eventId") eventId: String): RedirectView {
        // fix the event status and all events of its movie
        val bookedMovie = movieRepo.get(movieId)

        if (bookedMovie != null)
            bookingService.bookEvent(bookedMovie, ID(eventId))

        return RedirectView("/bookableMovies")
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/movie/bookmarkEvent")
    fun bookmarkEvent() = "/movielist"

    @PostMapping("/movie/bookmarkEvent")
    fun bookmarkEvent(@RequestParam("movieId") movieId: String,
                     @RequestParam("eventId") eventId: String): RedirectView {
        // fix the event status and all events of its movie
        val bookedMovie = movieRepo.get(movieId)

        if (bookedMovie != null)
            bookingService.bookmarkEvent(bookedMovie, ID(eventId))

        return RedirectView("/bookableMovies")
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/unbookMovie")
    fun unbookEvent() = "/bookedMovies"

    @PostMapping("/unbookMovie")
    fun unbookEvent(@RequestParam("movieId") movieId: String): RedirectView {
        // mark all events in unbooked movie as available
        val movieToUnbook = movieRepo.get(movieId)

        if (movieToUnbook != null)
            bookingService.unbookEvent(movieToUnbook)

        return RedirectView("/bookedMovies")
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/unbookmarkMovie")
    fun unbookmarkEvent() = "/bookedMovies"

    @PostMapping("/unbookmarkMovie")
    fun unbookmarkEvent(@RequestParam("movieId") movieId: String): RedirectView {
        // mark all events in unbooked movie as available
        val movieToUnbook = movieRepo.get(movieId)

        if (movieToUnbook != null)
            bookingService.unbookmarkEvent(movieToUnbook)

        return RedirectView("/bookedMovies")
    }

}
