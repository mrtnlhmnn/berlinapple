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
        val bookedOrBookmarkedMovies =
                movieRepo.getMovies({ it.booked || it.bookmarked })
                .sortedBy { it.getBookedOrBookmarkedEvent()!!.begin }
                .filter {
                    (filterDay == null) ||
                    // at least one of the movie's events is on the given date
                    it.getBookedOrBookmarkedEvent()!!.startsOn(filterDay)
                }

//TODO refactor this:
        if (bookedOrBookmarkedMovies.isEmpty()) {
            model.addAttribute("bookedMoviesPerDay", emptyList<Movie>())
            model.addAttribute("totalBookings", 0)
        }
        else {
            var idx = 0
            var initial = true
            var movieBefore: Movie? = null

            val bookedMoviesPerDay: MutableList<MutableList<Movie>> = arrayListOf(arrayListOf())
            for (movie in bookedOrBookmarkedMovies) {
                if (!initial) {
                    if (movie.getBookedOrBookmarkedEvent()!!.getBookingDay()!!.isAfter(
                        movieBefore!!.getBookedOrBookmarkedEvent()!!.getBookingDay())) {
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
            model.addAttribute("totalBookings", bookedOrBookmarkedMovies.size)
        }

        model.addAttribute("days", dayRepo.getDaysAsStrings())
        if (filterDay != null) model.addAttribute("filterDay", filterDay)

        return "bookedMovies"
    }
}
