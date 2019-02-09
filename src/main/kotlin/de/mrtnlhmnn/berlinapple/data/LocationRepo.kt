package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.application.JSONConvertable
import org.springframework.stereotype.Component

@Component
class LocationRepo: HashMap<String, Location>(), JSONConvertable