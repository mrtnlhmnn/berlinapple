package de.mrtnlhmnn.berlinapple.data

data class Movie (val id: ID, val title: String, val description: String, val prio: Int, val events: MutableList<Event>)