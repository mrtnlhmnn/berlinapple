package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.Movie
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.time.LocalDateTime

@Controller
class BookingController(val movieRepo: MovieRepo) {

    @RequestMapping("/bookableMovies")
    fun listBookableMovies(model: Model): String {

        var bookableMovies = movieRepo.values.toList().sortedByDescending { m -> m.prio }.toMutableList()

        // Remove all movies containing an already booked event.
        for (movie in bookableMovies) {
            for (event in movie.events){
                if (event.booked) bookableMovies.remove(movie)
            }
        }

        model.addAttribute("movies", bookableMovies)

        return "bookableMovies"
    }

    @RequestMapping("/bookableMovies/{id}")
    fun listBookableEvents(@PathVariable("id") id: String, model: Model): String {
        val movie = movieRepo.get(ID(id))
        val bookedTimes = getBookedTimes()

        for (event in movie!!.events){
            if ( (event.begin intersects bookedTimes) or (event.end intersects bookedTimes))
                movie.events.remove(event)
        }

        model.addAttribute("movie", movie)
        return "bookableEvents"
    }


    fun getBookedTimes(): List<Pair<LocalDateTime, LocalDateTime>>{

        var bookedTimes: MutableList<Pair<LocalDateTime, LocalDateTime>> = mutableListOf()

        for (movie in movieRepo.values){
            for (event in movie.events){
                if (event.booked) bookedTimes.add(Pair(event.begin, event.end))
            }
        }
        return bookedTimes
    }

    infix fun LocalDateTime.intersects (times: List<Pair<LocalDateTime, LocalDateTime>>) : Boolean {
        for (interval in times){
            if ((this >= interval.first) or (this <= interval.second)) return true
        }
        return false
    }
}