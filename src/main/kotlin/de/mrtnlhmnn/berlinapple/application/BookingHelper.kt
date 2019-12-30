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

    fun bookEvent(bookedMovie: Movie?, bookedEventID: ID){
        // fix the event status and all events of its movie
        var bookedEvent: Event? = null

        bookedMovie.let { movie ->
            val events = movie!!.events
            events.forEach { event ->
                if (event.id == bookedEventID) {
                    bookedEvent = event
                    bookedEvent?.status = EventStatus.BOOKED
                }
                else
                    event.status = EventStatus.UNAVAILABLE
            }
        }

        fixAllEvents(bookedMovie, bookedEvent)

        changed()
    }

    fun fixAllEvents(bookedMovie: Movie?, bookedEvent: Event?){
        // fix all other events of all other movies (especially set all events to unavailable,
        // if they intersect with the booked event)
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
