package de.mrtnlhmnn.berlinapple.infrastructure

import com.squareup.moshi.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.lang.Exception
import java.lang.reflect.Type
import java.net.URL
import com.squareup.moshi.JsonAdapter
import de.mrtnlhmnn.berlinapple.data.Location
import de.mrtnlhmnn.berlinapple.data.LocationRepo
import de.mrtnlhmnn.berlinapple.data.Movie
import de.mrtnlhmnn.berlinapple.data.MovieRepo

interface JSONConvertable

val jsonBuilder = Moshi.Builder()
        .add(JSONZonedDateTimeTypeAdapter())
        .add(URLTypeAdapter())
        .add(MovieRepoTypeAdapter())
        .add(LocationRepoTypeAdapter())
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
        val listType: Type = Types.newParameterizedType(List::class.java, T::class.java)
        val listAdapter: JsonAdapter<List<T>> = jsonBuilder.adapter(listType)
        return listAdapter.fromJson(this)
    }
    catch (ex: Exception) {
        System.err.println(ex)
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

private class LocationRepoTypeAdapter {
    @ToJson
    fun toJson(locationRepo: LocationRepo): List<Location> = locationRepo.values.toList()
}

