import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class ColorDisplay extends JFrame
{
	//Externally set components
	private JLabel lblCoordinates;
	private JLabel lblRGB;
	private JLabel lblHSV;
	private JLabel lblHex;
	private JPanel colorPanel;
	
	//Frame preferences
	private boolean openStatus = true;
	private boolean dynamic;

	public ColorDisplay(boolean[] displayOptions, boolean dynamic)
	{	
		//Create frame
		super();
		setPreferences();

		//Determine total number of visible panels
		int numOfPanelsVisible = sumVisibility(displayOptions);
		
		//Create panels
		JPanel displayPanel = createDisplayPanel(displayOptions, numOfPanelsVisible);

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
	
	private JPanel createDisplayPanel(boolean[] displayOptions, int numVisible)
	{
		//Create panels
		JPanel displayPanel = new JPanel();
		GridLayout layout = new GridLayout(numVisible, 1);
		displayPanel.setLayout(layout);
		JPanel[] panels = new JPanel[4];
		colorPanel = new JPanel();

		for (int i = 0; i < panels.length; i++)
		{
			panels[i] = new JPanel();
			panels[i].setLayout(new FlowLayout(FlowLayout.LEFT));
		}

		//Create labels
		JLabel lblDisplayCoord = new JLabel("X,Y = ");
		JLabel lblDisplayRGB = new JLabel("RGB = ");
		JLabel lblDisplayHSV = new JLabel("HSV = ");
		JLabel lblDisplayHex = new JLabel("Hex = ");
		lblCoordinates = new JLabel("(x, y)");
		lblRGB = new JLabel("(r, g, b)");
		lblHSV = new JLabel("(h, s, v)");
		lblHex = new JLabel("hex");

		//Change labels to fixed-width font
		Font monospaced = new Font("Monospaced", Font.BOLD, 12);
		lblDisplayCoord.setFont(monospaced);
		lblDisplayRGB.setFont(monospaced);
		lblDisplayHSV.setFont(monospaced);
		lblDisplayHex.setFont(monospaced);

		//Add labels to panels
		panels[0].add(lblDisplayCoord);
		panels[0].add(lblCoordinates);
		panels[1].add(lblDisplayRGB);
		panels[1].add(lblRGB);
		panels[2].add(lblDisplayHSV);
		panels[2].add(lblHSV);
		panels[3].add(lblDisplayHex);
		panels[3].add(lblHex);

		//Add smaller panels to display panel
		for (int i = 0; i < panels.length; i++)
			if (displayOptions[i])
				displayPanel.add(panels[i]);

		if (displayOptions[4])
			displayPanel.add(colorPanel);

		return displayPanel;
	}

	public void setText(String coordinates, String rgb, String hsv, String hex)
	{
		//Set label text
		lblCoordinates.setText(coordinates);
		lblCoordinates.repaint();

		lblRGB.setText(rgb);
		lblRGB.repaint();

		lblHSV.setText(hsv);
		lblHSV.repaint();

		lblHex.setText(hex);
		lblHex.repaint();

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
