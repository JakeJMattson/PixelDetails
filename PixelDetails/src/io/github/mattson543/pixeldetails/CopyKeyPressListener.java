package io.github.mattson543.pixeldetails;

import java.awt.event.*;

public class CopyKeyPressListener implements KeyListener
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
		shouldCopy = e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C;
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		//Unused but required
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		//Unused but required
	}

	public boolean wasCopyRequested()
	{
		return shouldCopy;
	}
}