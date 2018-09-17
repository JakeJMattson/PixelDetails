/**
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
package io.github.jakejmattson.pixeldetails;

import java.awt.event.*;
import java.io.Serializable;

/**
 * KeyListener to detect a keyboard copy request.
 *
 * @author JakeJMattson
 */
@SuppressWarnings("serial")
class CopyKeyPressListener implements KeyListener, Serializable
{
	/**
	 * Whether of not the user requested to copy the data
	 */
	private boolean shouldCopy;

	CopyKeyPressListener()
	{
		shouldCopy = false;
	}

	@Override
	public synchronized void keyPressed(KeyEvent e)
	{
		//Copy command (Ctrl + C) being pressed
		if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C)
			shouldCopy = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	boolean wasCopyRequested()
	{
		//Save current state
		boolean state = shouldCopy;

		//Reset state
		shouldCopy = false;

		return state;
	}
}