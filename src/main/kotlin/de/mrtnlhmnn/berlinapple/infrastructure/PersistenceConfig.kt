package de.mrtnlhmnn.berlinapple.infrastructure

import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.StringUtils
import de.mrtnlhmnn.berlinapple.data.*
import de.mrtnlhmnn.berlinapple.infrastructure.S3Config
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.time.Instant

@Configuration
class PersistenceConfig {

    @Value("\${offline:false}")
    val offline = false

    @Value(value = "\${persistence.savetos3.toggle:false}")
    var persistenceToggle: Boolean = false
}
