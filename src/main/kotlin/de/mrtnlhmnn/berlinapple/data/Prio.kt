package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.infrastructure.JSONConvertable

data class Prio(val value: Int): Comparable<Prio>, JSONConvertable  {
    override fun compareTo(other: Prio): Int {
        return (value - other.value)
    }

    override fun toString() = value.toString()

    companion object {
        val TOP    = Prio(99)
        val NORMAL = Prio(50)
        val LOW    = Prio(5)
        val NURFUERAKKREDITIERTE = Prio(1)
        val HIDE   = Prio(0)
    }

    fun isTop()    = this == TOP
    fun isNormal() = this == NORMAL
    fun isLow()    = this == LOW
    fun isHide()   = this == HIDE
}
