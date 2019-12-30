package de.mrtnlhmnn.berlinapple.infrastructure

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.StringUtils
import de.mrtnlhmnn.berlinapple.data.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.time.Instant

@Component
@EnableScheduling
class PersistenceToS3Scheduler(val movieRepo: MovieRepo,
                               val s3Config: S3Config,
                               val persistenceConfig: PersistenceConfig) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    // ---------------------------------------------------------------------------------------------------

    // remember the latest change

    private var timeStampOfLatestSave: Instant? = null
    private var timeStampOfLatestChange = Instant.now()

    fun changed() { timeStampOfLatestChange = Instant.now() }
    private fun hasChanged() = ( timeStampOfLatestSave == null || timeStampOfLatestChange.isAfter(timeStampOfLatestSave) )

    // ---------------------------------------------------------------------------------------------------

    @Scheduled(cron = "\${persistenceSchedule:0 * * * * *}")
    fun saveMoviesToS3() {
        if (! needToSaveToS3()) {
            LOGGER.info("PersistenceScheduler is not saving to S3, because toggled to false - or not necessary, as no changes done")
        }
        else {
            val movieRepoAsJson = movieRepo.toJSON()
            val bytes = movieRepoAsJson.toByteArray(StringUtils.UTF8)
            val bis = ByteArrayInputStream(bytes)

            val now = Instant.now().toEpochMilli().toString()
            val s3Key = s3Config.movieKeyPrefix + now + ".json"

            val metadata = ObjectMetadata()
            metadata.contentLength = bytes.size.toLong()

            try {
                val putRequest = PutObjectRequest(s3Config.s3BucketName, s3Key, ByteArrayInputStream(bytes), metadata)
                s3Config.s3Client().putObject(putRequest)
                timeStampOfLatestSave = Instant.now()
            }
            finally {
                bis.close()
            }

            LOGGER.info("PersistenceScheduler is writing {} movies with {} events as JSON file to S3 ... (to bucket/key {}/{}, {} bytes)",
                    movieRepo.getNumberOfMovies(),
                    movieRepo.getNumberOfEvents(),
                    s3Config.s3BucketName,
                    s3Key,
                    metadata.contentLength)
        }
    }

    fun togglePersistence (toggle: Boolean){
        persistenceConfig.persistenceToggle = toggle
        LOGGER.info("PersistenceScheduler toggle now switched to {}", persistenceConfig.persistenceToggle)
    }

    fun needToSaveToS3(): Boolean = (persistenceConfig.persistenceToggle && hasChanged())
}
