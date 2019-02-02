package de.mrtnlhmnn.berlinapple.data

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Event (val id: ID, val begin: ZonedDateTime, val end: ZonedDateTime,
                  val location: String, var booked: Boolean = false): Comparable<Event>
{
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun printBegin(): String {
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printEnd(): String {
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    // compare by begin date/time
    override fun compareTo(other: Event): Int {
        if (begin == other.begin) return 0
        if (begin.isAfter(other.begin)) return 1
        return -1
    }
}