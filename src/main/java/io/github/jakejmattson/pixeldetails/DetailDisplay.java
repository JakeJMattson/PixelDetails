package io.github.jakejmattson.pixeldetails;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import javax.swing.*;

/**
 * Displays the pixel information to users.
 *
 * @author JakeJMattson
 */
@SuppressWarnings("serial")
class DetailDisplay
{
	/**
	 * Frame to contain all GUI components
	 */
	private final JFrame frame;
	/**
	 * Array of panels containing actions (lambdas) to self-populate data
	 */
	private final ActionPanel[] panels;
	/**
	 * Panel to display the color of the pixel that the mouse pointer is over
	 */
	private JPanel colorPanel;
	/**
	 * Determines whether or not the frame has been closed
	 */
	private boolean isOpen = true;
	/**
	 * Determines whether or not the panel showing the color is visible
	 */
	private final boolean hasColorPanel;
	/**
	 * Determines whether or not to move the frame as the mouse moves
	 */
	private final boolean isDynamic;
	/**
	 * Determines whether or not to include static label text when copying
	 */
	private final boolean shouldCopyLabels;
	/**
	 * Listens for a request to copy the pixel information to the clip board
	 */
	private final CopyKeyPressListener copyListener;

	DetailDisplay(ActionPanel[] panels, boolean hasColorPanel, boolean isDynamic, boolean shouldCopyLabels)
	{
		//Create frame
		frame = new JFrame();

		//Initialize
		this.panels = panels;
		this.hasColorPanel = hasColorPanel;
		this.isDynamic = isDynamic;
		this.shouldCopyLabels = shouldCopyLabels;
		copyListener = new CopyKeyPressListener();

		//Set frame preferences
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(isDynamic);
		frame.addKeyListener(copyListener);
		frame.addWindowListener(createWindowListener());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		//Create panel
		JPanel displayPanel = createDisplayPanel();

		if (isDynamic)
			displayPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		//Add panel to frame
		frame.add(displayPanel);

		//Resize frame
		frame.pack();

		if (!isDynamic)
			frame.setSize((int) (frame.getWidth() * 1.4), frame.getHeight());

		//Show frame
		frame.setVisible(true);
	}

	/**
	 * Create a listener to monitor the frame closing event.
	 *
	 * @return WindowListener
	 */
	private WindowListener createWindowListener()
	{
		return new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent windowClosed)
			{
				isOpen = false;
			}
		};
	}

	/**
	 * Build a display panel composed of all panels requested by the user.
	 *
	 * @return The panel containing all info selected by the user
	 */
	private JPanel createDisplayPanel()
	{
		//Create panel
		JPanel displayPanel = new JPanel(new GridLayout(0, 1));

		for (JPanel panel : panels)
			displayPanel.add(panel);

		if (hasColorPanel)
		{
			colorPanel = new JPanel();
			displayPanel.add(colorPanel);
		}

		return displayPanel;
	}

	/**
	 * Set the position of the frame if dynamic placement was selected.
	 *
	 * @param framePosition
	 *            Point to represent the new location
	 */
	private void setPosition(Point framePosition)
	{
		//Get screen info
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();

		//Get frame info
		Rectangle frameBounds = frame.getBounds();
		int frameHeight = frameBounds.height;
		int frameWidth = frameBounds.width;

		//Calculate frame bounds
		int rightBound = (int) (framePosition.getX() + frameWidth);
		int lowerBound = (int) (framePosition.getY() + frameHeight);

		//Distance between display and mouse
		int buffer = 10;

		//Determine direction
		if (rightBound >= screenWidth)
			framePosition.x -= frameWidth + buffer;
		else
			framePosition.x += buffer;

		if (lowerBound >= screenHeight)
			framePosition.y -= frameHeight + buffer;
		else
			framePosition.y += buffer;

		frame.setLocation(framePosition);
	}

	/**
	 * Externally called to see if display frame is still open.
	 *
	 * @return Open status
	 */
	boolean isOpen()
	{
		return isOpen;
	}

	/**
	 * Copy pixel data to the clip board if requested by the user.
	 */
	void copyIfRequested()
	{
		if (copyListener.wasCopyRequested())
		{
			StringBuilder buffer = new StringBuilder();

			//Get data being displays
			for (ActionPanel panel : panels)
			{
				if (shouldCopyLabels)
					buffer.append(panel.getStaticLabelText());

				buffer.append(panel.getText()).append(System.lineSeparator());
			}

			//Send data to system clip board
			StringSelection stringSelection = new StringSelection(buffer.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		}
	}

	/**
	 * Update all components with current mouse and color information.
	 *
	 * @param mousePosition Current position of the mouse
	 * @param pixelColor Current color under the mouse pointer
	 */
	void updateComponents(Point mousePosition, Color pixelColor)
	{
		//Update panel text
		for (ActionPanel panel : panels)
			panel.performAction(mousePosition, pixelColor);

		//Update panel color
		if (hasColorPanel)
			colorPanel.setBackground(pixelColor);

		//Move frame to mouse position
		if (isDynamic)
			setPosition(mousePosition);

		//Resize frame
		if (isDynamic)
			frame.pack();
	}
}