package de.mrtnlhmnn.berlinapple.data

import de.mrtnlhmnn.berlinapple.application.JSONConvertable

data class Prio(val value: Int, val optional: String = ""): Comparable<Prio>, JSONConvertable
{
    override fun compareTo(other: Prio): Int {
        return (value - other.value)
    }

    override fun toString() = value.toString()

    companion object {
//TODO check when one needs const val, when only val?
        val TOP    = Prio(99)
        val NORMAL = Prio(50)
        val LOW    = Prio(1)
        val HIDE   = Prio(0)
    }

    fun isTop()    = this == TOP
    fun isNormal() = this == NORMAL
    fun isLow()    = this == LOW
    fun isHide()   = this == HIDE
}

