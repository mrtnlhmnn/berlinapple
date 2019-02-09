package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.*
import org.springframework.stereotype.Component

@Component
class BookingHelper(val movieRepo: MovieRepo){

    fun bookEvent(bookedMovie: Movie?, bookedEventID: ID){
        // fix the event status and all events of its movie
        var bookedEvent: Event? = null

        bookedMovie.let {
            val events = it!!.events
            events.forEach { ev ->
                if (ev.id == bookedEventID) {
                    bookedEvent = ev
                    bookedEvent?.status = EventStatus.BOOKED
                }
                else
                    ev.status = EventStatus.UNAVAILABLE
            }
        }

        fixAllEvents(bookedMovie, bookedEvent)
    }

    fun fixAllEvents(bookedMovie: Movie?, bookedEvent: Event?){
        // fix all other events of all other movies (especially set all events to unavailable,
        // if they intersect with the booked event)
        val movies = movieRepo.getSortedMovies()
        movies.forEach {
            if (it != bookedMovie) {
                val events = it.events
                events.forEach { ev ->
                    if (ev overlaps bookedEvent)
                        ev.status = EventStatus.UNAVAILABLE
                }
            }
        }
    }
}