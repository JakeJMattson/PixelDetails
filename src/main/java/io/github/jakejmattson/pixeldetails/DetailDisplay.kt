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
import java.awt.datatransfer.StringSelection
import java.awt.event.*

import javax.swing.*

internal class DetailDisplay(panels: Array<ActionPanel>,
							 private val hasColorPanel: Boolean,
							 private val isDynamic: Boolean,
							 private val shouldCopyLabels: Boolean) {

	private val frame: JFrame = JFrame()
	private val panels: Array<ActionPanel> = panels.clone()
	private var colorPanel: JPanel? = null
	private val copyListener: CopyKeyPressListener = CopyKeyPressListener()
	var isOpen = true
		private set

	init {
		frame.isAlwaysOnTop = true
		frame.isUndecorated = isDynamic
		frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		frame.isVisible = true
		frame.addKeyListener(copyListener)
		frame.addWindowListener(createWindowListener())
		frame.add(createDisplayPanel())
		frame.pack()

		if (!isDynamic)
			frame.setSize((frame.width * 1.4).toInt(), frame.height)
	}

	private fun createWindowListener(): WindowListener {
		return object: WindowAdapter() {
			override fun windowClosing(windowClosed: WindowEvent?) {
				isOpen = false
			}
		}
	}

	private fun createDisplayPanel(): JPanel {
		val displayPanel = JPanel(GridLayout(0, 1))

		panels.forEach { displayPanel.add(it) }

		if (hasColorPanel) {
			colorPanel = JPanel()
			displayPanel.add(colorPanel)
		}

		if (isDynamic)
			displayPanel.border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK)

		return displayPanel
	}

	private fun setPosition(framePosition: Point) {
		val screenSize = Toolkit.getDefaultToolkit().screenSize
		val frameBounds = frame.bounds
		val frameHeight = frameBounds.height
		val frameWidth = frameBounds.width

		val rightBound = (framePosition.getX() + frameWidth).toInt()
		val lowerBound = (framePosition.getY() + frameHeight).toInt()

		val buffer = 10

		if (rightBound >= screenSize.getWidth())
			framePosition.x -= frameWidth + buffer
		else
			framePosition.x += buffer

		if (lowerBound >= screenSize.getHeight())
			framePosition.y -= frameHeight + buffer
		else
			framePosition.y += buffer

		frame.location = framePosition
	}

	fun copyIfRequested() {
		if (copyListener.wasCopyRequested()) {
			val buffer = StringBuilder()

			panels.forEach { panel ->
				if (shouldCopyLabels)
					buffer.append(panel.staticLabelText)

				buffer.append(panel.text).append(System.lineSeparator())
			}

			val stringSelection = StringSelection(buffer.toString())
			Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, null)
		}
	}

	fun updateComponents(mousePosition: Point, pixelColor: Color) {
		panels.forEach { it.performAction(mousePosition, pixelColor) }

		if (hasColorPanel)
			colorPanel!!.background = pixelColor

		if (isDynamic) {
			setPosition(mousePosition)
			frame.pack()
		}
	}
}