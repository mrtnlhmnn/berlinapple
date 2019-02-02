package de.mrtnlhmnn.berlinapple.data

import java.net.URL

data class Movie (val id: ID, val title: String?, val description: String, var prio: Prio,
                  val url: URL?,
                  val events: MutableList<Event>)
{
    fun getSortedEvents(): MutableList<Event> {
        return events.sortedBy{it.begin}.toMutableList()
    }
}