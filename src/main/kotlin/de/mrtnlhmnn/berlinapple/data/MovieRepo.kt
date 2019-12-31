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
    fun getMovies(predicate: (Movie) -> Boolean) = values.filter(predicate)

    fun getSortedMovies() = values.toList()
        .sortedBy { it.events.first() }
        .sortedByDescending { it.prio }.toMutableList()
    fun getFilteredSortedMovies(predicate: (Movie) -> Boolean) = getSortedMovies().filter(predicate)
//TODO refactor this:
    fun getFilteredSortedMovies(filterDay: String?) =
        getSortedMovies().filter {
            (filterDay == null) ||
            // at least one of the movie's events is on the given date
            it.events.filter { it.startsOn(filterDay) }.isNotEmpty()
        }

    fun getNumberOfMovies() = this.values.size
    fun getNumberOfEvents(): Long {
        var counter = 0L
        this.values.map{
            counter += it.events.size
        }
        return counter
    }

    fun get(movie: Movie) = get(movie.id)
    fun get(id: String) = get(ID(id))
    fun addOrUpdate(movie: Movie) = put(movie.id, movie)
}
