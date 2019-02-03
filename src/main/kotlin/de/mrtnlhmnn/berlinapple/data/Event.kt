package de.mrtnlhmnn.berlinapple.data

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

enum class EventStatus  {
    AVAILABLE, UNAVAILABLE, BOOKED
}

data class Event (
        val id: ID,
        val begin: ZonedDateTime,
        val end: ZonedDateTime,
        val location: String,
        var status: EventStatus = EventStatus.AVAILABLE): Comparable<Event>, JSONConvertable
{
//TODO verallgemeinert, kann raus, da alles in JsonHelper generisch gebaut ist
/*
    companion object {
        fun toJSON(event: Event): String {
            val jsonAdapter: JsonAdapter<Event> = jsonBuilder.adapter(Event::class.java)
            return jsonAdapter.toJson(event)
        }

        fun fromJSON(json: String): Event? {
            val jsonAdapter: JsonAdapter<Event> = jsonBuilder.adapter(Event::class.java)
            return jsonAdapter.fromJson(json)
        }
    }
*/
    fun printBegin(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return begin.withZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter)
    }

    fun printEnd(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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

    // https://stackoverflow.com/questions/325933/determine-whether-two-date-ranges-overlap
    // (StartA <= EndB)  and  (EndA >= StartB)
    infix fun overlaps(other: Event?): Boolean {
        if (other == null) return false
        return (   (this.begin.isBefore(other.end) || this.begin == other.end)
                && (this.end.isAfter(other.begin)  || this.end   == other.begin))
    }
}
