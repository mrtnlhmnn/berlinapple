package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable
import java.net.URL

const val MOVIE_DESCRIPTION_DELIMITER = " | "

data class Movie (val title: String,
                  val rawDescription: String,
                  var prio: Prio,
                  val url: URL?): JSONConvertable
{
    val id = ID.createMovieID(title)
    val events: MutableList<Event> = mutableListOf()

    val description: String
        get() = replaceURLinDescriptionAndAddDelimiters(rawDescription)

    private fun replaceURLinDescriptionAndAddDelimiters(s: String): String {

        return try {
            val tmp = s.substringBefore("\nhttps://")  // delete URL
                       .replace("\n", MOVIE_DESCRIPTION_DELIMITER)           // replace all \n by |

            // ... and now replace last delimiter at the end (if any found)
            val lastIndexOfDELIM = tmp.lastIndexOf(MOVIE_DESCRIPTION_DELIMITER)
            if (lastIndexOfDELIM == -1) tmp else tmp.substring(0, lastIndexOfDELIM)
        }
        catch (ex: Exception) {
            s // Paranoia fallback to the original string
        }
    }

    val category: MovieCategory
        get() = MovieCategory.findCategory(description)

    val booked: Boolean
        get() = isOneEventBooked()
    fun isOneEventBooked(): Boolean {
        events.forEach { if (it.booked) return true }
        return false
    }

    val bookmarked: Boolean
        get() = isOneEventBookmarked()
    fun isOneEventBookmarked(): Boolean {
        events.forEach { if (it.bookmarked) return true }
        return false
    }

    val available: Boolean
        get() = isOneEventAvailable()
    fun numberOfEventsAvailable(): Int = events.count { it.available || it.bookmarked }
    fun isOneEventAvailable(): Boolean = numberOfEventsAvailable() > 0

    fun getSortedEvents(): MutableList<Event> {
        return events.sortedBy{it.begin}.toMutableList()
    }

    fun getBookedEvent(): Event? = getEvent{ it.booked }.firstOrNull()
    fun getBookmarkedEvent(): Event? = getEvent { it.bookmarked }.firstOrNull()
    fun getBookedOrBookmarkedEvent(): Event? = getEvent { it.booked || it.bookmarked }.firstOrNull()
    private fun getEvent(predicate: (Event) -> Boolean) = events.filter(predicate)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return (id == (other as Movie).id)
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
