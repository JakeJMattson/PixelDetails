package io.github.mattson543.pixeldetails;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Displays the pixel information to users
 *
 * @author mattson543
 */
@SuppressWarnings("serial")
public class DetailDisplay extends JFrame
{
	/**
	 * Array of labels that will be changed as the pixel under the mouse changes
	 */
	private JLabel[] dynamicLabels;
	/**
	 * Panel to display the color of the pixel that the mouse pointer is over
	 */
	private JPanel colorPanel;
	/**
	 * Determines whether or not the frame has been closed by the user
	 */
	private boolean isOpen = true;
	/**
	 * Determines whether or not to move the frame as the mouse moves
	 */
	private boolean isDynamic;

	public DetailDisplay(String[] labelText, boolean[] isPanelVisible, boolean dynamic)
	{
		//Create frame
		super();

		//Initialize
		isDynamic = dynamic;
		dynamicLabels = new JLabel[labelText.length];
		setFramePreferences();

		//Create panel
		JPanel displayPanel = createDisplayPanel(labelText, isPanelVisible);

		if (isDynamic)
			displayPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		//Add panel to frame
		this.add(displayPanel);

		//Show frame
		setVisible(true);
	}

	/**
	 * Determine the number of panels that the user has requested to be created
	 *
	 * @param isPanelVisible
	 *            Array of booleans that determine if the panel at the matching
	 *            index should be created
	 * @return Total number of visible panels
	 */
	private int countVisiblePanels(boolean[] isPanelVisible)
	{
		//Local variables
		int total = 0;

		//Sum visibilities from array
		for (boolean isVisible : isPanelVisible)
			total += isVisible ? 1 : 0;

		return total;
	}

	private void setFramePreferences()
	{
		//Set frame preferences
		setUndecorated(isDynamic);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent windowClosed)
			{
				isOpen = false;
			}
		});
	}

	/**
	 * Create all panels requested by the user
	 *
	 * @param labelText
	 *            Array of strings that act as labels for the data
	 * @param isPanelVisible
	 *            Array of booleans that determine if the panel at the matching
	 *            index should be created
	 * @return The panel containing all info selected by the user
	 */
	private JPanel createDisplayPanel(String[] labelText, boolean[] isPanelVisible)
	{
		//Determine the total number of visible panels
		int numVisible = countVisiblePanels(isPanelVisible);

		//Create panel
		JPanel displayPanel = new JPanel(new GridLayout(numVisible, 1));

		//Create label preferences
		Font labelFont = new Font("Monospaced", Font.BOLD, 12);

		for (int i = 0; i < labelText.length; i++)
			if (isPanelVisible[i])
			{
				//Create labels
				JLabel staticLabel = new JLabel(labelText[i]);
				staticLabel.setFont(labelFont);
				dynamicLabels[i] = new JLabel();

				//Add labels to panel
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel.add(staticLabel);
				panel.add(dynamicLabels[i]);

				//Add smaller panel to display panel
				displayPanel.add(panel);
			}

		if (isPanelVisible[labelText.length])
		{
			colorPanel = new JPanel();
			displayPanel.add(colorPanel);
		}

		return displayPanel;
	}

	/**
	 * Set all dynamic label text and resize the frame to fit it
	 *
	 * @param labelText
	 *            Array of strings to be displayed
	 */
	public void setDynamicLabelText(String[] labelText)
	{
		//Set label text
		for (int i = 0; i < labelText.length; i++)
			dynamicLabels[i].setText(labelText[i]);

		//Resize frame
		pack();
	}

	/**
	 * Set the background color of the color panel
	 *
	 * @param color
	 *            New panel color
	 */
	public void setPanelColor(Color color)
	{
		colorPanel.setBackground(color);
	}

	/**
	 * Set the position of the frame if dynamic placement was selected
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

	public boolean isOpen()
	{
		return isOpen;
	}
}