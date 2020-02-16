package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable
import org.springframework.stereotype.Component

@Component
class MovieRepo: HashMap<ID, Movie>(), JSONConvertable {
    fun findByTitleIgnoreCase(title: String?): Movie? {
        if (title == null || title == "") return null

        val movies = this.values.toList()

        for (movie in movies) {
            if (movie.title.toLowerCase() == title.toLowerCase()) return movie
        }
        return null
    }

    fun getMovies() = values

    // sort by these criteria:
    // - 1) priority, descending (higher prio is more important)
    // - 2) num events (fewer is more important, as it is harder to get tickets)
    // - 3) early movies (today is more important than tomorrow)
    // -> high-prio movies with few early events are on top!
    fun getSortedMovies() = getMovies().toList()
            .sortedBy { it.category }
            .sortedBy { it.getSortedEvents().first() }
            .sortedBy { it.events.size }
            .sortedByDescending { it.prio }
            .toMutableList()

    private fun dayFilter(filterDay: String?): (Movie) -> Boolean = {
        (filterDay == null)
        // at least one of the movie's events is on the given date
        || it.events.filter { it.startsOn(filterDay) }.isNotEmpty()
    }

    fun getMovies(predicate: (Movie) -> Boolean) = getMovies().filter(predicate)
    fun getMovies(filterDay: String?) = getMovies(dayFilter(filterDay))
    fun getFilteredSortedMovies(filterDay: String?) =
        getSortedMovies().filter(dayFilter(filterDay))

    fun getNumberOfMovies() = getMovies().size
    fun getNumberOfMovies(filterDay: String?) = getMovies(filterDay).size
    fun getNumberOfMovies(movies: List<Movie>) = movies.size

    fun getNumberOfEvents(): Long = getNumberOfEvents(this.values)
    fun getNumberOfEvents(filterDay: String?): Long = getNumberOfEvents(getMovies(filterDay))
    fun getNumberOfEvents(movieList: Collection<Movie>): Long {
//TODO Refactor this, might be easier with flatMap() ?
        var counter = 0L
        movieList.map {
            counter += it.events.size
        }
        return counter
    }

    fun get(movie: Movie) = get(movie.id)
    fun get(id: String) = get(ID(id))
    fun addOrUpdate(movie: Movie) = put(movie.id, movie)
}
