package de.mrtnlhmnn.berlinapple.application

import de.mrtnlhmnn.berlinapple.data.*
import de.mrtnlhmnn.berlinapple.infrastructure.S3Config
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Component
class PersistenceFromS3Reader(val s3Config: S3Config) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    val keyPrefix: String = "berlinapple2020/movies/movies-"

    fun getLastMovieListFromS3(): List<Movie> {
        val movieListAsString: String? = readLastMovieListPersistenceFromS3()
        return movieListAsString?.listFromJSON<Movie>() ?: emptyList()
    }

    private fun readLastMovieListPersistenceFromS3 (): String? {
        val s3FileList = s3Config.s3Client().listObjectsV2(s3Config.s3BucketName, keyPrefix)
        val lastMovieListFromS3 = s3FileList.objectSummaries.asSequence()
                .filter { it.size > 0 }
                .sortedBy { it.key }
                .lastOrNull()
        if (lastMovieListFromS3 == null) {
            LOGGER.info("Could not find any movie list on S3")
            return null
        }

        LOGGER.info("Now reading {} from S3", lastMovieListFromS3.key)
        val s3Object = s3Config.s3Client().getObject(s3Config.s3BucketName, lastMovieListFromS3.key)
        var inputStream: InputStream? = null
        try {
            inputStream = s3Object.objectContent
            val lastMovieListFromS3AsString = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
            return lastMovieListFromS3AsString
        }
        finally {
            if (inputStream != null) inputStream.close()
        }
    }
}
