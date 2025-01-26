package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.Movie
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class PersistenceFromDiskReader(val persistenceToDiskConfig: PersistenceToDiskConfig) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    fun getLastMovieListFromDisk(): List<Movie> {
        if (persistenceToDiskConfig.offline) return emptyList()

        val movieListAsString: String? = readLastMovieListFromDisk()
        return movieListAsString?.listFromJSON() ?: emptyList()
    }

    private fun readLastMovieListFromDisk (): String? {
        try {
            val path = Paths.get(persistenceToDiskConfig.filePathPrefix)
            val lastMovieListFromDisk = Files.list(path)
                .filter { Files.isRegularFile(it) }
                .filter { Files.size(it) > 0 }
                .filter { it.fileName.toString().endsWith(persistenceToDiskConfig.fileSuffix) }
                .sorted(Comparator.comparing { it.fileName.toString() })
                .toList()
                .lastOrNull()

            if (lastMovieListFromDisk == null) {
                LOGGER.warn("Could not find any movie list on disk")
                return null
            }

            LOGGER.info("Now reading {} from disk", lastMovieListFromDisk.fileName)
            return lastMovieListFromDisk.toFile().readText(Charsets.UTF_8)
        }
        catch (e: Exception) {
            LOGGER.error("Exception with retrieving data from disk", e)
            return null
        }
    }
}
