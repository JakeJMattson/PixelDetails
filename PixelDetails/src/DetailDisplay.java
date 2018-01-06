import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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

		//Determine total number of visible panels
		int numOfPanelsVisible = sumVisibility(displayOptions);
		
		//Create panels
		JPanel displayPanel = createDisplayPanel(labelText, displayOptions, numOfPanelsVisible);
		
		if (dynamic)
			displayPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));

		//Add panels to frame
		this.add(displayPanel);

		//Show frame
		this.setVisible(true);
	}

	private int sumVisibility(boolean[] panelVisibilities)
	{
		//Local variables
		int total = 0;
		
		//Sum visibilities from array
		for (int i = 0; i < panelVisibilities.length; i++)
			if (panelVisibilities[i])
				total++;
		
		return total;
	}

	private void setPreferences(boolean dynamic)
	{
		//Set frame preferences
		this.setUndecorated(dynamic);
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent windowClosed)
			{
				openStatus = false;
				dispose();
			}
		});
	}
	
	private JPanel createDisplayPanel(String[] labelText, boolean[] displayOptions, int numVisible)
	{
		//Create panels
		JPanel displayPanel = new JPanel();
		GridLayout layout = new GridLayout(numVisible, 1);
		displayPanel.setLayout(layout);
		JPanel[] panels = new JPanel[labelText.length + 1];
		colorPanel = new JPanel();

		for (int i = 0; i < panels.length; i++)
		{
			panels[i] = new JPanel();
			panels[i].setLayout(new FlowLayout(FlowLayout.LEFT));
		}		
		
		//Create labels
		dynamicLabels = new JLabel[labelText.length];
		JLabel[] staticLabels = new JLabel[labelText.length];
		Font labelFont = new Font("Monospaced", Font.BOLD, 12);
		
		for (int i = 0; i < labelText.length; i++)
		{
			if (displayOptions[i]) 
			{
				//Create labels
				staticLabels[i] = new JLabel(labelText[i]);
				staticLabels[i].setFont(labelFont);
				dynamicLabels[i] = new JLabel(labelText[i]);
				
				//Add labels to panels
				panels[i].add(staticLabels[i]);
				panels[i].add(dynamicLabels[i]);
				
				//Add smaller panels to display panel
				displayPanel.add(panels[i]);
			}
		}
		
		if (displayOptions[labelText.length])
			displayPanel.add(colorPanel);

		return displayPanel;
	}

	public void setDynamicLabelText(String[] labelText)
	{
		//Set label text
		for (int i = 0; i < labelText.length; i++)
		{
			if (dynamicLabels[i] != null) 
			{
				dynamicLabels[i].setText(labelText[i]);
				dynamicLabels[i].repaint();
			}
		}

		//Resize frame
		this.pack();
	}

	public void setColor(Color color)
	{
		//Set display color
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
		return this.openStatus;
	}
}