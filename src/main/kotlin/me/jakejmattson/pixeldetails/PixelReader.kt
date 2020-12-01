package me.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.JOptionPane
import kotlin.math.roundToInt
import kotlin.system.exitProcess

fun main() {
    val info = OptionPanel("Info to be displayed (Text)").apply {
        addCheckBox("Coordinates", "Mouse Location (X,Y)")
        addCheckBox("RGB", "Red, Green, Blue")
        addCheckBox("HSV", "Hue, Saturation, Value")
        addCheckBox("Hex", "Hexadecimal")
    }

    val color = OptionPanel("Color panel").apply {
        addCheckBox("Color panel", "Pixel color on a larger display")
    }

    val placement = OptionPanel("Placement").apply {
        addCheckBox("Dynamic", "Window will follow the mouse pointer")
    }

    val copy = OptionPanel("Copy format").apply {
        addCheckBox("Include labels", "Copy data labels with data")
    }

    displayOptions(info, color, placement, copy)

    val panels = createPanels(info.selections).takeIf { it.isNotEmpty() } ?: return
    val hasColorPanel = color.firstSelection()
    val isDynamic = placement.firstSelection()
    val shouldCopyLabels = copy.firstSelection()

    val display = Display(panels, hasColorPanel, isDynamic, shouldCopyLabels)
    val robot = Robot()

    while (display.isOpen) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y)

        display.updateComponents(mousePosition, pixelColor)
    }
}

private fun displayOptions(vararg options: OptionPanel) {
    val choice = JOptionPane.showOptionDialog(null, options, "Display options", JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE, null, arrayOf<Any>("Submit"), null)

    if (choice != JOptionPane.YES_OPTION)
        exitProcess(-1)
}

private fun createPanels(selections: BooleanArray) = selections.zip(listOf(
    ActionPanel("X,Y = ") { it.coordinates },
    ActionPanel("RGB = ") { it.rgb },
    ActionPanel("HSV = ") { it.hsv },
    ActionPanel("Hex = ") { it.hex }
)).filter { it.first }.map { it.second }