/*
 * Class Description:
 * GUI - Displays the pixel information to users
 */

package pixeldetails;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class DetailDisplay extends JFrame
{
	//Externally set components
	private JLabel[] dynamicLabels;
	private JPanel colorPanel;

	//Frame preferences
	private boolean openStatus = true;

	public DetailDisplay(String[] labelText, boolean[] displayOptions, boolean dynamic)
	{
		//Create frame
		super();
		setPreferences(dynamic);
		init(labelText.length);

		//Determine total number of visible panels
		int numOfPanelsVisible = sumVisibility(displayOptions);

		//Create panels
		JPanel displayPanel = createDisplayPanel(labelText, displayOptions, numOfPanelsVisible);

		if (dynamic)
			displayPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		//Add panels to frame
		this.add(displayPanel);

		//Show frame
		setVisible(true);
	}

	private void init(int numOfLabels)
	{
		dynamicLabels = new JLabel[numOfLabels];

		for (int i = 0; i < dynamicLabels.length; i++)
			dynamicLabels[i] = new JLabel();

		colorPanel = new JPanel();
	}

	private int sumVisibility(boolean[] panelVisibilities)
	{
		//Local variables
		int total = 0;

		//Sum visibilities from array
		for (boolean visible : panelVisibilities)
			total += visible ? 1 : 0;

		return total;
	}

	private void setPreferences(boolean dynamic)
	{
		//Set frame preferences
		setUndecorated(dynamic);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent windowClosed)
			{
				openStatus = false;
			}
		});
	}

	private JPanel createDisplayPanel(String[] labelText, boolean[] displayOptions, int numVisible)
	{
		//Create panel
		JPanel displayPanel = new JPanel(new GridLayout(numVisible, 1));

		//Create label preferences
		Font labelFont = new Font("Monospaced", Font.BOLD, 12);

		for (int i = 0; i < labelText.length; i++)
			if (displayOptions[i])
			{
				//Create label
				JLabel staticLabel = new JLabel(labelText[i]);
				staticLabel.setFont(labelFont);

				//Add labels to panel
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				panel.add(staticLabel);
				panel.add(dynamicLabels[i]);

				//Add smaller panel to display panel
				displayPanel.add(panel);
			}

		if (displayOptions[labelText.length])
			displayPanel.add(colorPanel);

		return displayPanel;
	}

	public void setDynamicLabelText(String[] labelText)
	{
		//Set label text
		for (int i = 0; i < labelText.length; i++)
			dynamicLabels[i].setText(labelText[i]);

		//Resize frame
		pack();
	}

	public void setColor(Color color)
	{
		colorPanel.setBackground(color);
	}

	public void setPosition(Point framePosition)
	{
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

	public boolean getStatus()
	{
		return openStatus;
	}
}