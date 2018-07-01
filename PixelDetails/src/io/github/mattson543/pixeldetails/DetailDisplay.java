package io.github.mattson543.pixeldetails;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

import javax.swing.*;

/**
 * Displays the pixel information to users.
 *
 * @author mattson543
 */
@SuppressWarnings("serial")
public class DetailDisplay extends JFrame
{
	/**
	 * Array of text from static labels - used when requested during copying
	 */
	private final String[] staticText;
	/**
	 * Array of labels that will be changed as the pixel under the mouse changes
	 */
	private final JLabel[] dynamicLabels;
	/**
	 * Panel to display the color of the pixel that the mouse pointer is over
	 */
	private JPanel colorPanel;
	/**
	 * Determines whether or not the frame has been closed
	 */
	private boolean isOpen = true;
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

	public DetailDisplay(String[] labelText, boolean[] isPanelVisible, boolean dynamic, boolean copyMode)
	{
		//Create frame
		super();

		//Initialize
		staticText = labelText;
		dynamicLabels = new JLabel[labelText.length];
		isDynamic = dynamic;
		shouldCopyLabels = copyMode;
		copyListener = new CopyKeyPressListener();

		//Set frame preferences
		setAlwaysOnTop(true);
		setUndecorated(isDynamic);
		addKeyListener(copyListener);
		addWindowListener(createWindowListener());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		//Create panel
		JPanel displayPanel = createDisplayPanel(isPanelVisible);

		if (isDynamic)
			displayPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		//Add panel to frame
		add(displayPanel);

		//Resize frame
		pack();

		if (!isDynamic)
			this.setSize((int) (getWidth() * 1.4), getHeight());

		//Show frame
		setVisible(true);
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
	 * Create all panels requested by the user.
	 *
	 * @param labelText
	 *            Array of strings that act as labels for the data
	 * @param isPanelVisible
	 *            Array of booleans that determine if the panel at the matching
	 *            index should be created
	 * @return The panel containing all info selected by the user
	 */
	private JPanel createDisplayPanel(boolean[] isPanelVisible)
	{
		//Create panel
		JPanel displayPanel = new JPanel(new GridLayout(0, 1));

		//Create label preferences
		Font labelFont = new Font("Monospaced", Font.BOLD, 12);

		for (int i = 0; i < staticText.length; i++)
			if (isPanelVisible[i])
			{
				//Create labels
				JLabel staticLabel = new JLabel(staticText[i]);
				staticLabel.setFont(labelFont);
				dynamicLabels[i] = new JLabel();

				//Add labels to panel
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel.add(staticLabel);
				panel.add(dynamicLabels[i]);

				//Add smaller panel to display panel
				displayPanel.add(panel);
			}

		if (isPanelVisible[staticText.length])
		{
			colorPanel = new JPanel();
			displayPanel.add(colorPanel);
		}

		return displayPanel;
	}

	/**
	 * Set all dynamic label text and resize the frame to fit it.
	 *
	 * @param labelText
	 *            Array of strings to be displayed
	 */
	public void setDynamicLabelText(String[] labelText)
	{
		//Set label text
		for (int i = 0; i < labelText.length; i++)
			if (dynamicLabels[i] != null)
				dynamicLabels[i].setText(labelText[i]);

		//Resize frame
		if (isDynamic)
			pack();
	}

	/**
	 * Set the background color of the color panel.
	 *
	 * @param color
	 *            New panel color
	 */
	public void setPanelColor(Color color)
	{
		if (colorPanel != null)
			colorPanel.setBackground(color);
	}

	/**
	 * Set the position of the frame if dynamic placement was selected.
	 *
	 * @param framePosition
	 *            Point to represent the new location
	 */
	public void setPosition(Point framePosition)
	{
		if (!isDynamic)
			return;

		//Get screen info
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();

		//Get frame info
		Rectangle frameBounds = this.getBounds();
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

		this.setLocation(framePosition);
	}

	/**
	 * Externally called to see if display frame is still open.
	 *
	 * @return Open status
	 */
	public boolean isOpen()
	{
		return isOpen;
	}

	/**
	 * Copy pixel data to the clip board if requested by the user.
	 */
	public void copyIfRequested()
	{
		if (copyListener.wasCopyRequested())
		{
			StringBuilder buffer = new StringBuilder();

			//Get data being displays
			for (int i = 0; i < dynamicLabels.length; i++)
			{
				JLabel currentLabel = dynamicLabels[i];

				if (currentLabel != null)
				{
					String data = currentLabel.getText();

					if (!data.trim().isEmpty())
					{
						if (shouldCopyLabels)
							buffer.append(staticText[i]);

						buffer.append(data + System.lineSeparator());
					}
				}
			}

			//Send data to system clip board
			StringSelection stringSelection = new StringSelection(buffer.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
		}
	}
}