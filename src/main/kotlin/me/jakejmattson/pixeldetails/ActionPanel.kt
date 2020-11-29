package me.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*

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

data class Pixel(val location: Point, val color: Color)