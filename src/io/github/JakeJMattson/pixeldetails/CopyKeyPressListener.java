package io.github.JakeJMattson.pixeldetails;

import java.awt.event.*;
import java.io.Serializable;

/**
 * KeyListner to detect a keyboard copy request.
 *
 * @author JakeJMattson
 */
@SuppressWarnings("serial")
public class CopyKeyPressListener implements KeyListener, Serializable
{
	/**
	 * Whether of not the user requested to copy the data
	 */
	private boolean shouldCopy;

	public CopyKeyPressListener()
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

	public boolean wasCopyRequested()
	{
		//Save current state
		boolean state = shouldCopy;

		//Reset state
		shouldCopy = false;

		return state;
	}
}