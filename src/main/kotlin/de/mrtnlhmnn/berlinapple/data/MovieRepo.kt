package de.mrtnlhmnn.berlinapple.data

import org.springframework.stereotype.Component

@Component
class MovieRepo: HashMap<ID, Movie>() {
    fun findByTitleIgnoreCase(title: String?): Movie? {
        if (title == null || title == "") return null

        val movies = this.values.toList()

        for (movie in movies) {
            if (movie.title?.toLowerCase() == title.toLowerCase()) return movie
        }
        return null
    }

    fun getSortedMovies() = values.toList()
                .sortedBy { it.events.first() }
                .sortedByDescending { it.prio }.toMutableList()

}