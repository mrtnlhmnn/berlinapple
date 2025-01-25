package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.*
import de.mrtnlhmnn.berlinapple.infrastructure.PersistenceToS3Scheduler
import org.springframework.stereotype.Component

@Component
class BookingService(val movieRepo: MovieRepo
                     //TODO no more S3 // ,val persistenceToS3Scheduler: PersistenceToS3Scheduler
) {

    fun fixStatusEvents(movie: Movie, bookedEvent: Event) {
        movie.events
                .filter  { it != bookedEvent }
                .forEach { it.status = EventStatus.UNAVAILABLE }
    }

    fun bookEvent(bookedMovie: Movie, bookedEventID: ID) {
        // fix the event status and all events of its movie
        var bookedEvent: Event? = null

        bookedMovie.events.forEach { event ->
            when (event.id) {
                bookedEventID -> {
                    bookedEvent = event
                    event.status = EventStatus.BOOKED
                }
                else ->
                    event.status = EventStatus.UNAVAILABLE
            }
        }
        setAllOverlappingEventsToUnvailable(bookedMovie, bookedEvent)

        //TODO no more S3 persistenceToS3Scheduler.changed()
    }

    // ---------------------------------------------------------------------------------------------------

    fun bookmarkEvent(bookmarkedMovie: Movie, bookmarkedEventID: ID){
        // fix the event status and all events of its movie
        var bookmarkedEvent: Event? = null

        bookmarkedMovie.events.forEach { event ->
            when (event.id) {
                bookmarkedEventID -> {
                    bookmarkedEvent = event
                    event.status = EventStatus.BOOKMARKED
                }
                else ->
                    event.status = EventStatus.UNAVAILABLE
            }
        }
        setAllOverlappingEventsToPotentiallyUnvailable(bookmarkedMovie, bookmarkedEvent)

        //TODO no more S3 persistenceToS3Scheduler.changed()
    }

    // ---------------------------------------------------------------------------------------------------

    fun unbookmarkEvent(movieToUnbookmark: Movie) = unbookEvent(movieToUnbookmark)

    fun unbookEvent(movieToUnbook: Movie) {
        movieToUnbook.let {
            val events = it.events
            events.forEach { ev -> ev.status = EventStatus.AVAILABLE }
        }

        // reset all availabilities of all movies and at the same time collect list of booked movies and events...
        val movies = movieRepo.getSortedMovies()
        val bookedMoviesAndEvents: MutableList<Pair<Movie, Event>> = mutableListOf()
        val bookmarkedMoviesAndEvents: MutableList<Pair<Movie, Event>> = mutableListOf()

        movies.forEach { movie ->
            // reset availability - only for movies that are NOT booked or bookmarked!
            // Events of booked or bookmarked movies must remain unavailable.
            if ( ! (movie.booked || movie.bookmarked) ) {
                movie.events.forEach { event ->
                    if (event.status == EventStatus.UNAVAILABLE || event.status == EventStatus.POTENTIALLY_UNAVAILABLE)
                        event.status =  EventStatus.AVAILABLE
                }
            }
            else {
                // collect booked movies and events...
                if (movie.booked)
                    bookedMoviesAndEvents.add(Pair(movie, movie.getBookedEvent()!!))
                if (movie.bookmarked)
                    bookmarkedMoviesAndEvents.add(Pair(movie, movie.getBookmarkedEvent()!!))
            }
         }

        // ... then fix them again  according to booked events
        bookmarkedMoviesAndEvents.forEach {
            setAllOverlappingEventsToPotentiallyUnvailable(it.first, it.second)
        }

        bookedMoviesAndEvents.forEach {
            setAllOverlappingEventsToUnvailable(it.first, it.second)
        }

        //TODO no more S3 persistenceToS3Scheduler.changed()
    }

    // ---------------------------------------------------------------------------------------------------

    private fun setAllOverlappingEventsToUnvailable(bookedMovie: Movie, bookedEvent: Event?){
        setAllOverlappingEventsToStatus(bookedMovie, bookedEvent, EventStatus.UNAVAILABLE)
    }

    private fun setAllOverlappingEventsToPotentiallyUnvailable(bookmarkedMovie: Movie, bookmarkedEvent: Event?){
        setAllOverlappingEventsToStatus(bookmarkedMovie, bookmarkedEvent, EventStatus.POTENTIALLY_UNAVAILABLE)
    }

    private fun setAllOverlappingEventsToStatus(markedMovie: Movie, markedEvent: Event?, status: EventStatus){
        // fix all other events of all other movies (especially set all events to status,
        //     if they intersect with the booked event)
        val movies = movieRepo.getSortedMovies().filter { !( it.booked || it.bookmarked )}
        movies.forEach { movie ->
            if (movie != markedMovie) {
                val events = movie.events
                events.forEach { event ->
                    if (event overlaps markedEvent)
                        if (event.status != EventStatus.UNAVAILABLE) // Unavailable is strongest!
                        event.status = status
                }
            }
        }
    }

}
