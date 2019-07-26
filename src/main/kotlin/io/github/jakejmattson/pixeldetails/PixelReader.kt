package io.github.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.JOptionPane
import kotlin.math.roundToInt

fun main() = setup()

private fun setup() {
    val info = OptionPanel("Info to be displayed (Text)")
        .addCheckBox("Coordinates", "Location (X,Y) of the mouse on the screen")
        .addCheckBox("RGB", "Pixel color as 'Red, Green, Blue' values")
        .addCheckBox("HSV", "Pixel color as 'Hue, Saturation, Value' values")
        .addCheckBox("Hex", "Pixel color as Hexadecimal value")

    val color = OptionPanel("Color panel")
        .addCheckBox("Color panel", "Pixel color on a larger display")

    val placement = OptionPanel("Placement behavior")
        .addCheckBox("Dynamic placement", "Allow the frame to \"follow\" the mouse pointer")

    val copy = OptionPanel("Copy format")
        .addCheckBox("Include labels", "Static labels will be copied along with dynamic data")

    //If submit button not clicked, exit program
    if (!displayOptions(info, color, placement, copy)) return

    val panels = createPanels(info.selections).takeIf { it.isNotEmpty() } ?: return
    val hasColorPanel = color.firstSelection()
    val isDynamic = placement.firstSelection()
    val shouldCopyLabels = copy.firstSelection()

    val display = DetailDisplay(panels, hasColorPanel, isDynamic, shouldCopyLabels)
    val robot = Robot()

    while (display.isOpen) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val pixelColor = robot.getPixelColor(mousePosition)

        display.updateComponents(mousePosition, pixelColor)
    }
}

private fun displayOptions(vararg options: OptionPanel) =
    JOptionPane.showOptionDialog(null, options, "Display options", JOptionPane.DEFAULT_OPTION,
        JOptionPane.PLAIN_MESSAGE, null, arrayOf<Any>("Submit"), null) == JOptionPane.YES_OPTION

private fun createPanels(selections: BooleanArray) = selections.zip(arrayOf(
        CoordinatePanel("X,Y = ") { mouse: Point-> "(%s, %s)".formatArray(mouse.toIntArray()) },
        ColorPanel("RGB = ") { pixelColor: Color -> "(%s, %s, %s)".formatArray(pixelColor.toIntArray()) },
        ColorPanel("HSV = ") { pixelColor: Color -> "(%s%%, %s%%, %s%%)".formatArray(pixelColor.toHSV()) },
        ColorPanel("Hex = ") { pixelColor: Color -> "#%02X%02X%02X".formatArray(pixelColor.toIntArray()) }
    )).filter { it.first }.map { it.second }

fun String.formatArray(data: Array<Int>) = String.format(this, *data as Array<out Any?>)
fun Robot.getPixelColor(point: Point) = getPixelColor(point.x, point.y)
fun Point.toIntArray() = arrayOf(x, y)
fun Color.toIntArray() = arrayOf(red, green, blue)
fun Color.toHSV() = FloatArray(3).apply { Color.RGBtoHSB(red, green, blue, this) }
        .map { (it * 100).roundToInt() }.toTypedArray()