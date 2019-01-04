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
import java.util.*
import javax.swing.*

fun main(args: Array<String>) {
    setup()
}

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

    val hasColorPanel = color.selections.first()
    val isDynamic = placement.selections.first()
    val shouldCopyLabels = copy.selections.first()

    val display = DetailDisplay(panels, hasColorPanel, isDynamic, shouldCopyLabels)
    val robot = Robot()

    while (display.isOpen) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y)

        display.updateComponents(mousePosition, pixelColor)
        display.copyIfRequested()
    }
}

private fun displayOptions(vararg options: OptionPanel): Boolean {
    val buttonText = arrayOf<Any>("Submit")
    val choice = JOptionPane.showOptionDialog(null, options, "Display options",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0])

    return choice == JOptionPane.YES_OPTION
}

private fun createPanels(selections: BooleanArray): ArrayList<ActionPanel>? {
    val panels = ArrayList<ActionPanel>()

    val actions = arrayOf(
            //Mouse Coordinates
            ActionPanel("X,Y = ") { mouse: Point, _: Color ->
                val coordinates = intArrayOf(mouse.x, mouse.y)
                format("(%s, %s)", coordinates)
            },
            //RGB values
            ActionPanel("RGB = ") { _: Point, pixelColor: Color ->
                format("(%s, %s, %s)", pixelColor.toIntArray())
            },
            //HSV values
            ActionPanel("HSV = ") { _: Point, pixelColor: Color ->
                format("(%s%%, %s%%, %s%%)", extractHSV(pixelColor.toIntArray()))
            },
            //Hex value
            ActionPanel("Hex = ") { _: Point, pixelColor: Color ->
                format("#%02X%02X%02X", pixelColor.toIntArray())
            }
    )

    selections.forEachIndexed { index: Int, isSelected: Boolean -> if (isSelected) panels.add(actions[index]) }
    return if (panels.isNotEmpty()) panels else null
}

private fun extractHSV(rgb: IntArray): IntArray {
    val hsb = FloatArray(rgb.size)
    Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb)
    return hsb.map { Math.round(it * 100) }.toIntArray()
}

private fun format(format: String, data: IntArray) = String.format(format, *data.map { it as Int? }.toTypedArray())

fun Color.toIntArray() = intArrayOf(this.red, this.green, this.blue)