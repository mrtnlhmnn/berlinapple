package de.mrtnlhmnn.berlinapple.application

import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.StringUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import de.mrtnlhmnn.berlinapple.data.*
import de.mrtnlhmnn.berlinapple.infrastructure.S3Config
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import sun.awt.CharsetString
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.reflect.Type
import java.net.URL
import java.time.Instant
import java.time.ZonedDateTime

@Component
@EnableScheduling
class PersistenceScheduler(val movieRepo: MovieRepo, val s3Config: S3Config) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    val keyPrefix: String = "berlinapple2019/movies/movies-"

    @Scheduled(cron = "\${persistenceSchedule:0 * * * * *}")
    fun saveMoviesToS3() {
        val movieRepoAsJson = movieRepo.toJSON()
        val bytes = movieRepoAsJson.toByteArray(StringUtils.UTF8)
        val bis = ByteArrayInputStream(bytes)

        val now = Instant.now().toEpochMilli().toString()
        val key = keyPrefix + now + ".txt"

        val metadata = ObjectMetadata()
        metadata.contentLength = bytes.size.toLong()

        try {
            val putRequest = PutObjectRequest(s3Config.s3BucketName, key, ByteArrayInputStream(bytes), metadata)
            s3Config.s3Client().putObject(putRequest)
        }
        finally {
            bis.close();
        }

        LOGGER.info("Writing {} movies (with {} events) as JSON to S3 with key {}, has {} bytes length",
                movieRepo.getNumberOfMovies(),
                movieRepo.getNumberOfEvents(),
                key, metadata.contentLength)
    }
}
