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

internal class CopyKeyPressListener: NativeKeyListener {
	private var shouldCopy: Boolean = false
	private var isCtrlDown: Boolean = false
	private var isCDown: Boolean = false

	override fun nativeKeyPressed(p0: NativeKeyEvent) {
		val key = p0.keyCode

		if (key == 46)
			isCDown = true
		else if (key == 29)
			isCtrlDown = true

		if (isCtrlDown && isCDown)
			shouldCopy = true
	}

	override fun nativeKeyReleased(p0: NativeKeyEvent) {
		val key = p0.keyCode

		if (key == 46)
			isCDown = false
		else if (key == 29)
			isCtrlDown = false

		if (!isCtrlDown || !isCDown)
			shouldCopy = false
	}

	override fun nativeKeyTyped(p0: NativeKeyEvent) {}

	fun wasCopyRequested(): Boolean {
		val state = shouldCopy
		shouldCopy = false
		return state
	}
}