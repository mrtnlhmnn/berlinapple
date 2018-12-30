package de.mrtnlhmnn.berlinapple

import de.mrtnlhmnn.berlinapple.data.MovieRepo
import de.mrtnlhmnn.berlinapple.data.ProgramParser

class TestMain

fun main(args: Array<String>) {
    val filename = "data/program.ics"
    println(message = ProgramParser(MovieRepo()).listProgram(filename))
}
