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

import org.jnativehook.*
import java.awt.*
import java.awt.event.*
import java.util.logging.*
import javax.swing.*

internal class DetailDisplay(private val panels: List<ActionPanel>,
							 hasColorPanel: Boolean,
							 private val isDynamic: Boolean,
							 shouldCopyLabels: Boolean) {

	private val colorPanel: JPanel? = if (hasColorPanel) JPanel() else null
	private val frame = JFrame().apply {
		isAlwaysOnTop = true
		isUndecorated = isDynamic
		defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
		isVisible = true
		addWindowListener(createWindowListener())
		add(createDisplayPanel())
		pack()

		if (!isDynamic)
			setSize((width * 1.4).toInt(), height)
	}
	var isOpen = true
		private set

	init {
		try {
			Logger.getLogger(GlobalScreen::class.java.getPackage().name).apply { level = Level.WARNING; useParentHandlers = false }
			GlobalScreen.registerNativeHook()
			GlobalScreen.addNativeKeyListener(CopyKeyPressListener(panels, shouldCopyLabels))
		} catch (e: NativeHookException) {
			println("Failed to register Native Hook. Copying will be disabled.")
		}
	}

	private fun createWindowListener() = object: WindowAdapter() {
		override fun windowClosing(windowClosed: WindowEvent?) {
			isOpen = false
		}
	}

	private fun createDisplayPanel() = JPanel(GridLayout(0, 1)).apply {
			panels.forEach { add(it) }

			if (colorPanel != null)
				add(colorPanel)

			if (isDynamic)
				border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK)
		}

	private fun setPosition(framePosition: Point) {
		val screenSize = Toolkit.getDefaultToolkit().screenSize
		val frameHeight = frame.bounds.height
		val frameWidth = frame.bounds.width
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

	fun updateComponents(mousePosition: Point, pixelColor: Color) {
		panels.forEach { it.performAction(mousePosition, pixelColor) }

		if (colorPanel != null)
			colorPanel.background = pixelColor

		if (isDynamic) {
			setPosition(mousePosition)
			frame.pack()
		}
	}
}