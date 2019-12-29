package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.application.BookingHelper
import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
class ScheduleController(val movieRepo: MovieRepo, val bookingHelper: BookingHelper) {
    @RequestMapping("/bookedMovies")
    fun listBookedMovies(model: Model): String {
        val bookedMovies = movieRepo.values.toList()
                .filter { it.booked }
                .sortedBy { it.getBookedEvent().begin }

        if (bookedMovies.isEmpty()) {
            model.addAttribute("bookedMoviesPerDay", emptyList<Movie>())
            model.addAttribute("totalBookings", 0)
        }
        else {
            var idx = 0
            var initial = true
            var movieBefore: Movie? = null

            val bookedMoviesPerDay: MutableList<MutableList<Movie>> = arrayListOf(arrayListOf())
            for (movie in bookedMovies) {
                if (!initial) {
                    if (movie.getBookedEvent().getBookingDay()!!.isAfter(
                        movieBefore!!.getBookedEvent().getBookingDay())) {
                        idx++
                    }
                }
                initial = false
                movieBefore = movie

                if (bookedMoviesPerDay.size == idx) {
                    bookedMoviesPerDay.add(arrayListOf(movie))
                } else {
                    bookedMoviesPerDay.get(idx).add(movie)
                }
            }
            model.addAttribute("bookedMoviesPerDay", bookedMoviesPerDay)
            model.addAttribute("totalBookings", bookedMovies.size)
        }
        return "bookedMovies"
    }
}
