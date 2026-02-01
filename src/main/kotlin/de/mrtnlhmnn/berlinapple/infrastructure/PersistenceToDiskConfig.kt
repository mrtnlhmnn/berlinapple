package de.mrtnlhmnn.berlinapple.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
open class PersistenceToDiskConfig {

    @Value("\${offline:true}")
    val offline = true

    @Value("\${movieFile.filePathPrefix:/var/data/berlinapple2026/movies}")
    val filePathPrefix = "/var/data" // as configured in Render's persistent disk

    @Value("\${movieFile.fileNamePrefix:movies-}")
    val fileNamePrefix = "movies-"

    @Value("\${movieFile.fileSuffix:.json}")
    val fileSuffix = ".json"

}
