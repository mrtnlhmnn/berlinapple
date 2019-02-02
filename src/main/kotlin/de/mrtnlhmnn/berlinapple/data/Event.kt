package de.mrtnlhmnn.berlinapple.data

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class Event (val id: ID, val begin: ZonedDateTime, val end: ZonedDateTime,
                  val location: String, var status: EventStatus = EventStatus.AVAILABLE): Comparable<Event> {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun printBegin(): String {
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    fun printEnd(): String {
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    val booked: Boolean
        get() = status == EventStatus.BOOKED
    val available: Boolean
        get() = status == EventStatus.AVAILABLE
    val unavailable: Boolean
        get() = status == EventStatus.UNAVAILABLE

    // compare by begin date/time
    override fun compareTo(other: Event): Int {
        if (begin == other.begin) return 0
        if (begin.isAfter(other.begin)) return 1
        return -1
    }

    infix fun overlaps(other: Event?): Boolean {
        if (other == null) return false

        // https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
        // (StartA <= EndB)  and  (EndA >= StartB)
        return (
                   (this.begin.isBefore(other.end) || this.begin == other.end)
                && (this.end.isAfter(other.begin)  || this.end   == other.begin))
    }
}

enum class EventStatus  {
    AVAILABLE, UNAVAILABLE, BOOKED
}