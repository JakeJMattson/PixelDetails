import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ColorDisplay extends JFrame
{
	//Externally set components
	private JLabel[] displayLabels;
	private JPanel colorPanel;
	
	//Frame preferences
	private boolean openStatus = true;
	private boolean dynamic;

	public ColorDisplay(String[] labelText, boolean[] displayOptions, boolean dynamic)
	{	
		//Create frame
		super();
		setPreferences();

		//Determine total number of visible panels
		int numOfPanelsVisible = sumVisibility(displayOptions);
		
		//Create panels
		JPanel displayPanel = createDisplayPanel(labelText, displayOptions, numOfPanelsVisible);

		//Add panels to frame
		this.add(displayPanel);
		
		//Set class variables
		this.dynamic = dynamic;
		
		//Set static size
		if (!dynamic)
		{
			this.pack();
			Dimension dim = this.getSize();
			dim.width *= 1.25;
			this.setSize(dim);
		}
		
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

	private void setPreferences()
	{
		//Set frame preferences
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
		displayLabels = new JLabel[labelText.length];
		JLabel[] labels = new JLabel[labelText.length];
		Font labelFont = new Font("Monospaced", Font.BOLD, 12);
		
		for (int i = 0; i < labelText.length; i++)
		{
			if (displayOptions[i]) 
			{
				//Create labels
				labels[i] = new JLabel(labelText[i]);
				labels[i].setFont(labelFont);
				displayLabels[i] = new JLabel(labelText[i]);
				
				//Add labels to panels
				panels[i].add(labels[i]);
				panels[i].add(displayLabels[i]);
				
				//Add smaller panels to display panel
				displayPanel.add(panels[i]);
			}
		}
		
		if (displayOptions[labelText.length])
			displayPanel.add(colorPanel);

		return displayPanel;
	}

	public void setLabelText(String[] labelText)
	{
		//Set label text
		for (int i = 0; i < labelText.length; i++)
		{
			if (displayLabels[i] != null) 
			{
				displayLabels[i].setText(labelText[i]);
				displayLabels[i].repaint();
			}
		}

		//Resize frame
		if (dynamic)
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