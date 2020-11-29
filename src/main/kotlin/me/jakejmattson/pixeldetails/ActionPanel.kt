package me.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*
import kotlin.math.roundToInt

class ActionPanel(val labelText: String, private val action: (Pixel) -> String) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    private val dynamicLabel = JLabel()

    var text: String
        get() = dynamicLabel.text
        set(value) {
            dynamicLabel.text = value
        }

    init {
        this.add(JLabel(labelText).apply { font = Font("Monospaced", Font.BOLD, 12) })
        this.add(dynamicLabel)
    }

    fun performAction(pixel: Pixel) {
        text = action.invoke(pixel)
    }
}

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