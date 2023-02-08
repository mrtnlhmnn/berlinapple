package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.*
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.InputStream
import java.nio.charset.StandardCharsets

@Component
class PersistenceFromS3Reader(val s3Config: S3Config) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    fun getLastMovieListFromS3(): List<Movie> {
        if (s3Config.offline) return emptyList()

        val movieListAsString: String? = readLastMovieListFromS3()
        return movieListAsString?.listFromJSON() ?: emptyList()
    }

    private fun readLastMovieListFromS3 (): String? {
        val s3FileList = s3Config.s3Client().listObjectsV2(s3Config.s3BucketName, s3Config.movieKeyPrefix)
        val lastMovieListFromS3 = s3FileList.objectSummaries.asSequence()
                .filter { it.size > 0 }
                .sortedBy { it.key }
                .lastOrNull()
        if (lastMovieListFromS3 == null) {
            LOGGER.warn("Could not find any movie list on S3")
            return null
        }

        LOGGER.info("Now reading {}/{} from S3", s3Config.s3BucketName, lastMovieListFromS3.key)
        val s3Object = s3Config.s3Client().getObject(s3Config.s3BucketName, lastMovieListFromS3.key)
        var inputStream: InputStream? = null
        try {
            inputStream = s3Object.objectContent
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8)
        }
        finally {
            inputStream?.close()
        }
    }
}
