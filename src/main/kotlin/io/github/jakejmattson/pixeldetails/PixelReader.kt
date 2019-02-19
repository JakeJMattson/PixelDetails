/*
 * The MIT License
 * Copyright © 2017 Jake Mattson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.JOptionPane

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

    val panels = createPanels(info.selections) ?: return
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

private fun displayOptions(vararg options: OptionPanel): Boolean {
    val buttonText = arrayOf<Any>("Submit")
    val choice = JOptionPane.showOptionDialog(null, options, "Display options",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0])

    return choice == JOptionPane.YES_OPTION
}

private fun createPanels(selections: BooleanArray) = selections.zip(arrayOf(
        ActionPanel("X,Y = ") { mouse: Point, _: Color -> "(%s, %s)".formatArray(mouse.toIntArray()) },
        ActionPanel("RGB = ") { _: Point, pixelColor: Color -> "(%s, %s, %s)".formatArray(pixelColor.toIntArray()) },
        ActionPanel("HSV = ") { _: Point, pixelColor: Color -> "(%s%%, %s%%, %s%%)".formatArray(pixelColor.toHSV()) },
        ActionPanel("Hex = ") { _: Point, pixelColor: Color -> "#%02X%02X%02X".formatArray(pixelColor.toIntArray()) }
    )).filter { it.first }.map { it.second }.takeIf { it.isNotEmpty() }

fun String.formatArray(data: IntArray) = String.format(this, *data.map { it as Int? }.toTypedArray())
fun Robot.getPixelColor(point: Point) = getPixelColor(point.x, point.y)
fun Point.toIntArray() = intArrayOf(x, y)
fun Color.toIntArray() = intArrayOf(red, green, blue)
fun Color.toHSV() = FloatArray(3).apply { Color.RGBtoHSB(red, green, blue, this) }
        .map { Math.round(it * 100) }.toIntArray()