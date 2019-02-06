package de.mrtnlhmnn.berlinapple.application

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import de.mrtnlhmnn.berlinapple.data.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import java.net.URL
import java.time.ZonedDateTime

@Component
@EnableScheduling
class PersistenceScheduler(val movieRepo: MovieRepo) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(cron = "\${persistenceSchedule:0 * * * * *}")
    fun saveMovies() {
        val event = Event( ID("11"), ZonedDateTime.now(), ZonedDateTime.now(), "here", EventStatus.BOOKED)
        val eventAsJson = event.toJSON()
        val parsedEvent = eventAsJson.fromJSON<Event>()
        println(event)
        println(eventAsJson)
        println(parsedEvent)
        println(parsedEvent == event)

        val movie = Movie(ID("12"), "title", "description", Prio.TOP, URL("http://localhost:8080"), mutableListOf(event))
        val movieAsJson = movie.toJSON()
        val parsedMovie = movieAsJson.fromJSON<Movie>()
        println(movie)
        println(movieAsJson)
        println(parsedMovie)
        println(parsedMovie == movie)

        val movieRepoAsJson = movieRepo.toJSON()
        println(movieRepoAsJson)

        val movieList = movieRepoAsJson.listFromJSON<Movie>()
        movieList!!.forEach {
            println(it.description)
            it.events.forEach {
                println(" --> " + it)
            }
        }
    }
}