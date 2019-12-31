package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.application.BookingService
import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class ScheduleController(val movieRepo: MovieRepo, val dayRepo: DayRepo, val bookingService: BookingService) {
    @RequestMapping("/bookedMovies")
    fun listBookedMovies(model: Model,
                         @RequestParam(required=false, name="filterDay") filterDay: String?): String {
        val bookedMovies = movieRepo.getMovies({ it.booked })
                .sortedBy { it.getBookedEvent().begin }
                .filter {
                    (filterDay == null) ||
                    // at least one of the movie's events is on the given date
                    it.getBookedEvent().startsOn(filterDay)
                }

//TODO refactor this:
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

        model.addAttribute("days", dayRepo.getDaysAsStrings())

        return "bookedMovies"
    }
}
