package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class BookingHelper(val movieRepo: MovieRepo) {

    // remember the latest change
    private var latestChange = Instant.now()
    private fun changed() { latestChange = Instant.now() }
    fun hasChangedSince(timestamp: Instant?) = ( timestamp == null || latestChange.isAfter(timestamp) )

    // ---------------------------------------------------------------------------------------------------

    fun bookEvent(bookedMovie: Movie, bookedEventID: ID){
        // fix the event status and all events of its movie
        var bookedEvent: Event? = null

        bookedMovie.let { movie ->
            val events = movie!!.events
            events.forEach { event ->
                if (event.id == bookedEventID) {
                    bookedEvent = event
                    bookedEvent?.status = EventStatus.BOOKED
                }
                else {
                    event.status = EventStatus.UNAVAILABLE
                }
            }
        }

        setAllOverlappingEventsToUnvailable(bookedMovie, bookedEvent)

        changed()
    }

    // ---------------------------------------------------------------------------------------------------

    fun unbookEvent(movieToUnbook: Movie){
        movieToUnbook.let {
            val events = it.events
            events.forEach { ev -> ev.status = EventStatus.AVAILABLE }
        }

        // reset all availabilities of all movies and at the same time collect list of booked movies and events...
        val movies = movieRepo.getSortedMovies()
        val bookedMoviesAndEvents: MutableList<Pair<Movie, Event>> = mutableListOf()

        movies.forEach { movie ->
            // reset availability - only for movies that are NOT booked! Events of booked movies must remain unavailable.
            if ( ! movie.booked) {
                movie.events.forEach { event ->
                    if (event.status == EventStatus.UNAVAILABLE)
                        event.status =  EventStatus.AVAILABLE
                }
            }
            else {
                // collect booked movies and events...
                bookedMoviesAndEvents.add(Pair(movie, movie.getBookedEvent()))
            }
         }

        // ... then fix them again them according to booked events
        bookedMoviesAndEvents.forEach {
            setAllOverlappingEventsToUnvailable(it.first, it.second)
        }

        changed()
    }

    // ---------------------------------------------------------------------------------------------------

    private fun setAllOverlappingEventsToUnvailable(bookedMovie: Movie, bookedEvent: Event?){
        // fix all other events of all other movies (especially set all events to unavailable,
        //     if they intersect with the booked event)
        val movies = movieRepo.getSortedMovies()
        movies.forEach { movie ->
            if (movie != bookedMovie) {
                val events = movie.events
                events.forEach { event ->
                    if (event overlaps bookedEvent)
                        event.status = EventStatus.UNAVAILABLE
                }
            }
        }
    }
}
