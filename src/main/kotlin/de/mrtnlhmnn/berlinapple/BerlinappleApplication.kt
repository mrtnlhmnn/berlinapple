package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.security.User
import de.mrtnlhmnn.berlinapple.security.UserRepo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BerlinappleApplication

fun main(args: Array<String>) {
    val userRepo = UserRepo()
    userRepo.put("1", User("1", "martin"))
    userRepo.put("2", User("1", "kristine"))

    runApplication<BerlinappleApplication>(*args)
}

