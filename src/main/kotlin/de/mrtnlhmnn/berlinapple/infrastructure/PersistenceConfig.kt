package de.mrtnlhmnn.berlinapple.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class PersistenceConfig {

    @Value("\${offline:false}")
    val offline = false

    @Value(value = "\${persistence.savetos3.toggle:false}")
    var persistenceToggle: Boolean = false
}
