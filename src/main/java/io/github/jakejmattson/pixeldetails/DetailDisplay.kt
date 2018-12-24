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

/**
 * Displays the pixel information to users.
 *
 * @author JakeJMattson
 */
internal class DetailDisplay(panels: Array<ActionPanel>,
	/**
	 * Determines whether or not the panel showing the color is visible
	 */
	private val hasColorPanel: Boolean,
	/**
	 * Determines whether or not to move the frame as the mouse moves
	 */
	private val isDynamic: Boolean,
	/**
	 * Determines whether or not to include static label text when copying
	 */
	private val shouldCopyLabels: Boolean) {
	/**
	 * Frame to contain all GUI components
	 */
	private val frame: JFrame
	/**
	 * Array of panels containing actions (lambdas) to self-populate data
	 */
	private val panels: Array<ActionPanel>
	/**
	 * Panel to display the color of the pixel that the mouse pointer is over
	 */
	private var colorPanel: JPanel? = null
	/**
	 * Determines whether or not the frame has been closed
	 */
	/**
	 * Externally called to see if display frame is still open.
	 *
	 * @return Open status
	 */
	var isOpen = true
		private set
	/**
	 * Listens for a request to copy the pixel information to the clip board
	 */
	private val copyListener: CopyKeyPressListener

	init {
		this.frame = JFrame()
		this.panels = panels.clone()
		this.copyListener = CopyKeyPressListener()

		val displayPanel = createDisplayPanel()

		if (isDynamic)
			displayPanel.border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK)

		frame.isAlwaysOnTop = true
		frame.isUndecorated = isDynamic
		frame.addKeyListener(copyListener)
		frame.addWindowListener(createWindowListener())
		frame.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		frame.add(displayPanel)
		frame.pack()

		if (!isDynamic)
			frame.setSize((frame.width * 1.4).toInt(), frame.height)

		frame.isVisible = true
	}

	/**
	 * Create a listener to monitor the frame closing event.
	 *
	 * @return WindowListener
	 */
	private fun createWindowListener(): WindowListener {
		return object: WindowAdapter() {
			override fun windowClosing(windowClosed: WindowEvent?) {
				isOpen = false
			}
		}
	}

	/**
	 * Build a display panel composed of all panels requested by the user.
	 *
	 * @return The panel containing all info selected by the user
	 */
	private fun createDisplayPanel(): JPanel {
		val displayPanel = JPanel(GridLayout(0, 1))

		for (panel in panels)
			displayPanel.add(panel)

		if (hasColorPanel) {
			colorPanel = JPanel()
			displayPanel.add(colorPanel)
		}

		return displayPanel
	}

	/**
	 * Set the position of the frame if dynamic placement was selected.
	 *
	 * @param framePosition
	 * Point to represent the new location
	 */
	private fun setPosition(framePosition: Point) {
		val screenSize = Toolkit.getDefaultToolkit().screenSize
		val screenWidth = screenSize.getWidth()
		val screenHeight = screenSize.getHeight()

		val frameBounds = frame.bounds
		val frameHeight = frameBounds.height
		val frameWidth = frameBounds.width

		val rightBound = (framePosition.getX() + frameWidth).toInt()
		val lowerBound = (framePosition.getY() + frameHeight).toInt()

		val BUFFER = 10

		if (rightBound >= screenWidth)
			framePosition.x -= frameWidth + BUFFER
		else
			framePosition.x += BUFFER

		if (lowerBound >= screenHeight)
			framePosition.y -= frameHeight + BUFFER
		else
			framePosition.y += BUFFER

		frame.location = framePosition
	}

	/**
	 * Copy pixel data to the clip board if requested by the user.
	 */
	fun copyIfRequested() {
		if (copyListener.wasCopyRequested()) {
			val buffer = StringBuilder()

			for (panel in panels) {
				if (shouldCopyLabels)
					buffer.append(panel.staticLabelText)

				buffer.append(panel.text).append(System.lineSeparator())
			}

			val stringSelection = StringSelection(buffer.toString())
			Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, null)
		}
	}

	/**
	 * Update all components with current mouse and color information.
	 *
	 * @param mousePosition Current position of the mouse
	 * @param pixelColor Current color under the mouse pointer
	 */
	fun updateComponents(mousePosition: Point, pixelColor: Color) {
		for (panel in panels)
			panel.performAction(mousePosition, pixelColor)

		if (hasColorPanel)
			colorPanel!!.background = pixelColor

		if (isDynamic) {
			setPosition(mousePosition)
			frame.pack()
		}
	}
}