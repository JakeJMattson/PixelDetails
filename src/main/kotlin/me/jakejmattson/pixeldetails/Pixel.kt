package me.jakejmattson.pixeldetails

import java.awt.*
import kotlin.math.roundToInt

data class Pixel(val location: Point, val color: Color) {
    val coordinates
        get() = "(%s, %s)".format(location.x, location.y)

    val rgb
        get() = "(%s, %s, %s)".format(color)

    val hsv
        get() = "(%s%%, %s%%, %s%%)".format(*color.toHSV())

    val hex
        get() = "#%02X%02X%02X".format(color)

    private fun String.format(color: Color) = with (color) { format(red, green, blue) }

    private fun Color.toHSV() = FloatArray(3)
        .apply { Color.RGBtoHSB(red, green, blue, this) }
        .map { (it * 100).roundToInt() }
        .toTypedArray()
}