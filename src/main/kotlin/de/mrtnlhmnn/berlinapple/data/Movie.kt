package de.mrtnlhmnn.berlinapple.data

data class Movie (val id: ID, val title: String, val description: String, var prio: Int, val events: MutableList<Event>)