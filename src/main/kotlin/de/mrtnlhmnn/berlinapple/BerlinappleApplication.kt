package de.mrtnlhmnn.berlinapple

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BerlinappleApplication(val config: BerlinappleConfig)

fun main(args: Array<String>) {
    val context = runApplication<BerlinappleApplication>(*args)
}

