package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class EventStatus  {
    AVAILABLE, UNAVAILABLE, POTENTIALLY_UNAVAILABLE, BOOKED
}

data class Event (val begin: ZonedDateTime, val end: ZonedDateTime,
                  val location: Location?,
                  var status: EventStatus = EventStatus.AVAILABLE): Comparable<Event>, JSONConvertable
{
    val id = ID.createEventID(begin.dayOfMonth.toString())

//TODO Refactor all 6 formatters (actually: 3, as they are redundant) out of the methods, but then JSON serializer needs to be aware not to store them!

    fun printBeginDateTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printBeginDateTimeForCalendarFile(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
        return begin.withZoneSameInstant(ZoneId.of("UTC")).format(formatter)
    }
    fun printBeginTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    fun printEndDateTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return end.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }
    fun printEndDateTimeForCalendarFile(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
        return end.withZoneSameInstant(ZoneId.of("UTC")).format(formatter)
    }
    fun printEndTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
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
    val potentiallyUnavailable: Boolean
        get() = status == EventStatus.POTENTIALLY_UNAVAILABLE

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

//TODO make buffer time configurable
        val bufferInMinutes = 30L
        return (   (this.begin.isBefore(other.end.plusMinutes(bufferInMinutes)))
                && (this.end.isAfter(other.begin.minusMinutes(bufferInMinutes))  ))
    }

    // only check, if event starts on a given date (in format 'yyyy-MM-dd')
    fun startsOn(date: String): Boolean {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return (begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter) == date)
    }
}
