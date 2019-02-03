package de.mrtnlhmnn.berlinapple.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import com.squareup.moshi.ToJson

private class JSONZonedDateTimeTypeAdapter {
    @ToJson
    fun toJson(zdt: ZonedDateTime): String {
        return zdt.toInstant().toEpochMilli().toString()
    }

    @FromJson
    fun fromJson(jdt: String): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jdt.toLong()), ZoneId.of("Europe/Berlin"))
    }
}

val jsonBuilder = Moshi.Builder().add(JSONZonedDateTimeTypeAdapter()).build()

// --------------------------------------------------------------------------------------------------------------

interface JSONConvertable

inline fun <reified T: JSONConvertable> T.toJSON(): String = jsonBuilder.adapter(T::class.java).toJson(this)

inline fun <reified T: JSONConvertable> String.fromJSON(): T? = jsonBuilder.adapter(T::class.java).fromJson(this)

//TODO verallgemeinern, wieder einfuehren, kommt noch von Gson
//inline fun <reified T: JSONConvertable> String.toObject(): T = gson.fromJson(this, T::class.java)
