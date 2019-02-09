package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.application.JSONConvertable
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class EventStatus  {
    AVAILABLE, UNAVAILABLE, BOOKED
}

data class Event (
        val id: ID,
        val begin: ZonedDateTime, val end: ZonedDateTime,
        val location: Location?,
        var status: EventStatus = EventStatus.AVAILABLE): Comparable<Event>, JSONConvertable
{
    fun printBeginDateTime(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printBeginDateTimeForCaldendarFile(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printBeginTime(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    fun printEndDateTime(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printEndDateTimeForCaldendarFile(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmZ")
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printEndTime(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    fun getBookingDay(): LocalDate? {
        if (!booked) return null
        return LocalDate.of(begin.year, begin.month, begin.dayOfMonth)
    }

    fun printBookingTime(): String {
        val begin = printBeginTime()
        val end = printEndTime()
        return "${begin} - ${end}"
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

    // https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
    // (StartA <= EndB)  and  (EndA >= StartB)
    infix fun overlaps(other: Event?): Boolean {
        if (other == null) return false

        val bufferInMinutes = 30L

//TODO make buffer time configurable
        return (   (this.begin.isBefore(other.end.plusMinutes(bufferInMinutes)))
                && (this.end.isAfter(other.begin.minusMinutes(bufferInMinutes))  ))
    }
}
