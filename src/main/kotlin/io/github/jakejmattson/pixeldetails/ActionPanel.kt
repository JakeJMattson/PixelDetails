package io.github.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*

internal class ActionPanel(val labelText: String, private val action: (mouse: Point, pixelColor: Color) -> String):
		JPanel(FlowLayout(FlowLayout.LEFT)) {

	private var dynamicLabel = JLabel()

	val text: String
		get() = dynamicLabel.text

	init {
		add(JLabel(labelText).apply { font = Font("Monospaced", Font.BOLD, 12) })
		add(dynamicLabel)
	}

	fun performAction(mousePosition: Point, pixelColor: Color) {
		dynamicLabel.text = action.invoke(mousePosition, pixelColor)
	}
}