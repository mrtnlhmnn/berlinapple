package de.mrtnlhmnn.berlinapple.ui

import de.mrtnlhmnn.berlinapple.data.Event
import de.mrtnlhmnn.berlinapple.data.ID
import de.mrtnlhmnn.berlinapple.data.Movie
import de.mrtnlhmnn.berlinapple.data.MovieRepo
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.time.ZonedDateTime

@Controller
class BookingController(val movieRepo: MovieRepo) {

    @RequestMapping("/bookableMovies")
    fun listBookableMovies(model: Model): String {

        val movies =
                movieRepo.values.toList()
                        .sortedBy { it.events.first() }
                        .sortedByDescending { it.prio }.toMutableList()

        // Remove all movies containing an already booked event.
        val bookableMovies = ArrayList<Movie>()
        bookableMovies.addAll(movies)

        for (movie in movies) {
             for (event in movie.events){
                 if (event.booked) {
                     bookableMovies.remove(movie)
                     break
                 }
            }
        }
        model.addAttribute("movies", bookableMovies)

        return "bookableMovieList"
    }

    @RequestMapping("/bookableEvents/{id}")
    fun listBookableEvents(@PathVariable("id") id: String, model: Model): String {
        val movie = movieRepo.get(ID(id))
        val bookedTimes = getBookedTimes()

        val bookeableEvents = ArrayList<Event>()

        for (event in movie!!.events){
            if ( ! (event.begin intersects bookedTimes) or (event.end intersects bookedTimes))
                bookeableEvents.add(event)
        }


        // val movieToDisplay = Movie(movie.id, movie.title, movie.description, movie.prio, movie.url,  bookeableEvents)
        val movieToDisplay = movie.copy(events =  bookeableEvents)

        model.addAttribute("movie", movieToDisplay)
        return "bookableEventsForOneMovie"
    }


    @GetMapping("/bookableEvents/bookEvent")
    fun book() = "/bookableEvents/{movie.id}"

    @PostMapping("/bookableEvents/bookEvent")
    fun book(@RequestParam("movieId") movieId: String,
             @RequestParam("eventId") eventId: String): RedirectView {
        return try {
            val movie = movieRepo.get(ID(movieId))
            val events = movie!!.events

            events.forEach{e -> if (e.id == ID(eventId))  e.booked = true  }

            movieRepo.put(movie.id, movie)

            RedirectView("/bookableMovies")
        } catch (exception: Exception) {
            RedirectView("/")
        }
    }


    fun getBookedTimes(): List<Pair<ZonedDateTime, ZonedDateTime>>{

        var bookedTimes: MutableList<Pair<ZonedDateTime, ZonedDateTime>> = mutableListOf()

        for (movie in movieRepo.values){
            for (event in movie.events){
                if (event.booked) bookedTimes.add(Pair(event.begin, event.end))
            }
        }
        return bookedTimes
    }

    infix fun ZonedDateTime.intersects (times: List<Pair<ZonedDateTime, ZonedDateTime>>) : Boolean {
        for (interval in times){
            if ((this >= interval.first) and (this <= interval.second)) return true
        }
        return false
    }
}