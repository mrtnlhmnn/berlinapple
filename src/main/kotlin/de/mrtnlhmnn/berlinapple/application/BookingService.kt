package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.*
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceToS3Scheduler
import org.springframework.stereotype.Component

@Component
class BookingService(val movieRepo: MovieRepo,
                     val persistenceToS3Scheduler: PersistenceToS3Scheduler) {

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

        persistenceToS3Scheduler.changed()
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

        persistenceToS3Scheduler.changed()
    }

    // ---------------------------------------------------------------------------------------------------

    private fun setAllOverlappingEventsToUnvailable(bookedMovie: Movie, bookedEvent: Event?){
        setAllOverlappingEventsToStatus(bookedMovie, bookedEvent, EventStatus.UNAVAILABLE)
    }

    private fun setAllOverlappingEventsToPoteniallyUnvailable(bookmarkedMovie: Movie, bookmarkedEvent: Event?){
        setAllOverlappingEventsToStatus(bookmarkedMovie, bookmarkedEvent, EventStatus.POTENTIALLY_UNAVAILABLE)
    }

    private fun setAllOverlappingEventsToStatus(markedMovie: Movie, markedEvent: Event?, status: EventStatus){
        // fix all other events of all other movies (especially set all events to status,
        //     if they intersect with the booked event)
        val movies = movieRepo.getSortedMovies()
        movies.forEach { movie ->
            if (movie != markedMovie) {
                val events = movie.events
                events.forEach { event ->
                    if (event overlaps markedEvent)
                        event.status = status
                }
            }
        }
    }

}
