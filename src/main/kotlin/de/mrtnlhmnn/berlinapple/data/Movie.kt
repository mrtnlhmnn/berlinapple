package de.mrtnlhmnn.berlinapple.data

import java.net.URL

data class Movie (val id: ID, val title: String, val description: String, var prio: Int,
                  val url: URL?,
                  val events: MutableList<Event>){
}