package de.mrtnlhmnn.berlinapple.data

import java.time.LocalDateTime

data class Event (val id: String, val movie: Movie, val begin: LocalDateTime, val end: LocalDateTime, val location: String)