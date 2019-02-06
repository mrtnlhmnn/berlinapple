package de.mrtnlhmnn.berlinapple.data

import com.squareup.moshi.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.lang.Exception
import java.lang.reflect.Type
import java.net.URL
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types.newParameterizedType

interface JSONConvertable

val jsonBuilder = Moshi.Builder()
        .add(JSONZonedDateTimeTypeAdapter())
        .add(URLTypeAdapter())
        .add(MovieRepoTypeAdapter())
        .build()

inline fun <reified T: JSONConvertable> T.toJSON(): String = jsonBuilder.adapter(T::class.java).toJson(this)

inline fun <reified T: JSONConvertable> String.fromJSON(): T? {
    try {
        return jsonBuilder.adapter(T::class.java).fromJson(this)
    }
    catch (ex: Exception) {
        return null;
    }
}

inline fun <reified T: JSONConvertable> String.listFromJSON(): List<T>? {
    try {
        val listMovieType: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val movieListAdapter: JsonAdapter<List<T>> = jsonBuilder.adapter(listMovieType)
        return movieListAdapter.fromJson(this)
    }
    catch (ex: Exception) {
        return null;
    }
}

// --------------------------------------------------------------------------------------------------------------

private class JSONZonedDateTimeTypeAdapter {
    @ToJson
    fun toJson(zdt: ZonedDateTime): String {
        return zdt.toInstant().toEpochMilli().toString()
    }

    @FromJson
    fun fromJson(s: String): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(s.toLong()), ZoneId.of("Europe/Berlin"))
    }
}
private class URLTypeAdapter {
    @ToJson
    fun toJson(url: URL): String = url.toString()

    @FromJson
    fun fromJson(s: String): URL = URL(s)
}

private class MovieRepoTypeAdapter {
    @ToJson
    fun toJson(movieRepo: MovieRepo): List<Movie> = movieRepo.values.toList()
}

