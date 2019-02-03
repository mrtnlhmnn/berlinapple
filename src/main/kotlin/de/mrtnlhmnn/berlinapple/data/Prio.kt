package de.mrtnlhmnn.berlinapple.data

data class Prio(val value: Int, val optional: String = ""): Comparable<Prio> {
    override fun compareTo(other: Prio): Int {
        return (value - other.value)
    }

    override fun toString() = ""+value

    companion object {
//TODO Wann braucht man const val, wann nur val?
        val TOP = Prio(99)
        val NORMAL = Prio(50)
        val LOW = Prio(1)
        val HIDE = Prio(0)
    }

    fun isTop()    = this == TOP
    fun isNormal() = this == NORMAL
    fun isLow()    = this == LOW
    fun isHide()   = this == HIDE
}

