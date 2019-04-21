package io.github.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*

abstract class ActionPanel(val labelText: String) : JPanel(FlowLayout(FlowLayout.LEFT)) {
	private var dynamicLabel = JLabel()

	var text: String
		get() = dynamicLabel.text
		set(value) {dynamicLabel.text = value}

	init {
		add(JLabel(labelText).apply { font = Font("Monospaced", Font.BOLD, 12) })
		add(dynamicLabel)
	}

	abstract fun performAction(data: Any)
}

class CoordinatePanel(labelText: String, private val action: (mouse: Point) -> String) : ActionPanel(labelText) {
	override fun performAction(data: Any) {
		text = action.invoke(data as Point)
	}
}

class ColorPanel(labelText: String, private val action: (pixelColor: Color) -> String) : ActionPanel(labelText) {
	override fun performAction(data: Any) {
		text = action.invoke(data as Color)
	}
}