package de.mrtnlhmnn.berlinapple.infrastructure

import de.mrtnlhmnn.berlinapple.data.*
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import kotlin.io.path.createDirectories


@Component
@EnableScheduling
class PersistenceScheduler(val movieRepo: MovieRepo,
                           val persistenceToDiskConfig: PersistenceToDiskConfig,
                           val persistenceConfig: PersistenceConfig) {
    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    // ---------------------------------------------------------------------------------------------------

    // remember the latest change

    private var timeStampOfLatestSave: Instant? = null
    private var timeStampOfLatestChange = Instant.now()

    fun changed() { timeStampOfLatestChange = Instant.now() }
    private fun hasChanged() = ( timeStampOfLatestSave == null || timeStampOfLatestChange.isAfter(timeStampOfLatestSave) )

    // ---------------------------------------------------------------------------------------------------

    @Scheduled(cron = "\${persistenceSchedule:0 0/5 * * * *}")
    fun saveMoviesToDisk() {
        if (! needToSaveToDisk()) {
            LOGGER.info("PersistenceScheduler is not saving to disk, because toggled to false - or not necessary, as no changes done")
        }
        else {
            val movieRepoAsJson = movieRepo.toJSON()

            val now = Instant.now().toEpochMilli().toString()
            val fileName =
                persistenceToDiskConfig.filePathPrefix + "/" +
                persistenceToDiskConfig.fileNamePrefix + now +
                persistenceToDiskConfig.fileSuffix
            val filePath = Paths.get(fileName)

            Files.createDirectories(filePath.parent)

            val file = FileWriter(fileName)
            try {
                file.write(movieRepoAsJson)
                timeStampOfLatestSave = Instant.now()
            }
            finally {
                file.close()
            }

            LOGGER.info("PersistenceScheduler is writing {} movies with {} events as JSON file to disk ... (to {}, {} bytes)",
                    movieRepo.getNumberOfMovies(),
                    movieRepo.getNumberOfEvents(),
                    File(fileName).canonicalPath,
                    movieRepoAsJson.length)
        }
    }

    fun togglePersistence (toggle: Boolean){
        persistenceConfig.persistenceToggle = toggle
        LOGGER.info("PersistenceScheduler toggle now switched to {}", persistenceConfig.persistenceToggle)
    }

    fun needToSaveToDisk(): Boolean =
        !persistenceConfig.offline
                && persistenceConfig.persistenceToggle
                && hasChanged()
}
