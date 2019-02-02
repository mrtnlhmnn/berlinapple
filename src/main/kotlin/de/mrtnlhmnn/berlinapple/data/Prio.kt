package de.mrtnlhmnn.berlinapple.data

val PRIO_TOP = 99
val PRIO_NORMAL = 50
val PRIO_LOW = 1
val PRIO_HIDE = 0

data class Prio(val value: Int, val optional: String = ""): Comparable<Prio> {
    override fun compareTo(other: Prio): Int {
        return (value - other.value)
    }

    override fun toString() = ""+value

    fun isTop() = value == 99
    fun isNormal() = value == 50
    fun isLow() = value == 1
    fun isHide() = value == 0
}

