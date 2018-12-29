package de.mrtnlhmnn.berlinapple.security

import de.mrtnlhmnn.berlinapple.security.User
import org.springframework.stereotype.Component

@Component
class UserRepo: HashMap<String, User>()