package de.mrtnlhmnn.berlinapple.data

data class Prio(val value: Int, val optional: String = ""): Comparable<Prio> {
    override fun compareTo(other: Prio): Int {
        return (value - other.value)
    }

    override fun toString() = ""+value

    companion object {
        val TOP = Prio(99)
        val NORMAL = Prio(50)
        val LOW = Prio(1)
        val HIDE = Prio(0)
    }

    fun isTop() = value == 99
    fun isNormal() = value == 50
    fun isLow() = value == 1
    fun isHide() = value == 0
}

