package de.mrtnlhmnn.berlinapple.data

import java.net.URL

data class Movie (val id: ID, val title: String?, val description: String, var prio: Prio,
                  val url: URL?,
                  val events: MutableList<Event>): JSONConvertable
{
    val booked: Boolean
        get() = isOneEventOfTheMovieBooked()
    fun isOneEventOfTheMovieBooked(): Boolean {
        events.forEach { if (it.booked) return true }
        return false
    }

    val available: Boolean
        get() = isOneEventOfTheMovieAvailable()
    fun isOneEventOfTheMovieAvailable(): Boolean {
        events.forEach { if (it.available) return true }
        return false
    }

    fun getSortedEvents(): MutableList<Event> {
        return events.sortedBy{it.begin}.toMutableList()
    }
}