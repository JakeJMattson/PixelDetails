package me.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.JOptionPane
import kotlin.system.exitProcess

fun main() {
    val info = OptionPanel("Display") {
        option("Coordinates", "Mouse Location (X,Y)")
        option("RGB", "Red, Green, Blue")
        option("HSV", "Hue, Saturation, Value")
        option("Hex", "Hexadecimal")
        option("Color", "Color display")
    }

    val extras = OptionPanel("Options") {
        option("Follow Mouse", "Window will follow the mouse pointer")
        option("Copy Labels", "Copy data labels with the selected data")
    }

    displayOptions(info, extras)

    val panels = createPanels(info.selections).takeIf { it.isNotEmpty() } ?: return
    val display = Display(panels, info.selections.last(), extras[0], extras[1])
    val robot = Robot()

    while (display.isOpen) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y)

        display.updateComponents(mousePosition, pixelColor)
    }
}

private fun displayOptions(vararg options: OptionPanel) {
    val choice = JOptionPane.showOptionDialog(null, options, "Display options",
        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, arrayOf("Submit"), null)

    if (choice != JOptionPane.YES_OPTION)
        exitProcess(-1)
}

private fun createPanels(selections: List<Boolean>) = selections.zip(listOf(
    ActionPanel("X,Y = ") { it.coordinates },
    ActionPanel("RGB = ") { it.rgb },
    ActionPanel("HSV = ") { it.hsv },
    ActionPanel("Hex = ") { it.hex }
)).filter { it.first }.map { it.second }