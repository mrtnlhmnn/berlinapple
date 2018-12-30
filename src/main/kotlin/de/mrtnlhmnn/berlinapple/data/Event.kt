package de.mrtnlhmnn.berlinapple.data

import java.time.LocalDateTime

data class Event (val id: ID, val begin: LocalDateTime, val end: LocalDateTime, val location: String)