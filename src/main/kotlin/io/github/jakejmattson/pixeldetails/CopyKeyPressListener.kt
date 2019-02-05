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

import org.jnativehook.keyboard.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

internal class CopyKeyPressListener(private val panels: List<ActionPanel>, private val shouldCopyLabels: Boolean): NativeKeyListener {
	private var isCtrlDown: Boolean = false
	private var isCDown: Boolean = false

	override fun nativeKeyPressed(event: NativeKeyEvent) {
		when (event.keyCode) {
			29 -> isCtrlDown = true
			46 -> isCDown = true
		}

		if (isCtrlDown && isCDown)
			copy()
	}

	override fun nativeKeyReleased(event: NativeKeyEvent) {
		when (event.keyCode) {
			29 -> isCtrlDown = false
			46 -> isCDown = false
		}
	}

	override fun nativeKeyTyped(event: NativeKeyEvent) {}

	private fun copy() {
		val text = StringBuilder().apply {
			panels.forEach { panel ->
				if (shouldCopyLabels)
					append(panel.staticLabelText)

				append(panel.text).append(System.lineSeparator())
			}
		}.toString()

		Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
	}
}