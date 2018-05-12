package io.github.mattson543.pixeldetails;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Performs pixel reading and formats data into strings.
 *
 * @author mattson543
 */
public class PixelReader
{
	public static void main(String[] args)
	{
		PixelReader driver = new PixelReader();
		driver.setup();
	}

	/**
	 * Create all necessary components and start the program.
	 */
	public void setup()
	{
		//Allow user to choose display options
		String title = "Display options";
		String[] options = {"Coordinates", "RGB", "HSV", "Hex", "Color bar"};

		//Get user choices
		boolean[][] displayChoices = displayOptions(title, options);

		//If submit button not clicked, exit program
		if (displayChoices == null)
			return;

		//Separate array
		boolean[] infoChoices = displayChoices[0];
		boolean isDynamic = displayChoices[1][0];
		boolean copyMode = displayChoices[2][0];

		//Create display
		String[] staticLabelText = {"X,Y = ", "RGB = ", "HSV = ", "Hex = "};
		DetailDisplay display = new DetailDisplay(staticLabelText, infoChoices, isDynamic, copyMode);

		//Start program
		start(display);
	}

	/**
	 * Allow the user to choose what data they want to see.
	 *
	 * @param title
	 *            The title displayed at the top of the option pane
	 * @param infoOptions
	 *            The array of choices to be displayed to the user
	 * @return A boolean array of user selections
	 */
	private static boolean[][] displayOptions(String title, String[] infoOptions)
	{
		ArrayList<JCheckBox> boxes = new ArrayList<>();

		//Prepare args
		String[] placementOptions = {"Dynamic placement"};
		String[] copyOptions = {"Include labels"};

		//Create panels
		JPanel infoPanel = createCheckBoxPanel("Info to be displayed", boxes, infoOptions);
		JPanel dynamicPanel = createCheckBoxPanel("Placement behavior", boxes, placementOptions);
		JPanel copyPanel = createCheckBoxPanel("Copy format", boxes, copyOptions);

		//Get choices from user
		Object[] components = {infoPanel, dynamicPanel, copyPanel};
		Object[] buttonText = {"Submit"};
		int choice = JOptionPane.showOptionDialog(null, components, title, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);

		boolean[][] selections = null;

		if (choice == JOptionPane.YES_OPTION)
		{
			selections = new boolean[3][];
			selections[0] = new boolean[infoOptions.length];
			selections[1] = new boolean[placementOptions.length];
			selections[2] = new boolean[copyOptions.length];

			int index = 0;

			for (int i = 0; i < selections.length; i++)
				for (int j = 0; j < selections[i].length; j++)
					selections[i][j] = boxes.get(index++).isSelected();
		}

		return selections;
	}

	/**
	 * Create a panel that will hold check boxes.
	 *
	 * @param title
	 *            "Title" of titled border
	 * @param options
	 * @param boxes
	 * @return Panel
	 */
	private static JPanel createCheckBoxPanel(String title, ArrayList<JCheckBox> boxes, String[] options)
	{
		//Create new panel with a vertical layout
		JPanel panel = new JPanel(new GridLayout(0, 1));

		//Create a titled border around the panel
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title,
				TitledBorder.LEFT, TitledBorder.TOP));

		//Create check boxes
		for (String option : options)
		{
			//Create new check box and check it
			JCheckBox box = new JCheckBox(option);
			box.setSelected(true);

			//Add box to list
			boxes.add(box);

			//Add box to panel
			panel.add(box);
		}

		return panel;
	}

	/**
	 * Create a new robot and handle the potential exception.
	 *
	 * @return Robot
	 */
	private Robot createRobot()
	{
		try
		{
			return new Robot();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to create Robot. Please try again.", "Internal Error!",
					JOptionPane.ERROR_MESSAGE);

			return null;
		}
	}

	/**
	 * Gets pixel info, formats it, then sends all information to the display.
	 *
	 * @param display
	 *            The display frame that is shown to the user
	 */
	private void start(DetailDisplay display)
	{
		//Create robot to get pixel color
		Robot robot = createRobot();

		while (display.isOpen())
		{
			//Get base info
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			Color pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y);
			int[] coord = {mousePosition.x, mousePosition.y};
			int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
			int[] hsvData = extractHSV(rgbInfo);

			//Create strings to display
			String pos = format("(%s, %s)", coord);
			String rgb = format("(%s, %s, %s)", rgbInfo);
			String hsv = format("(%s%%, %s%%, %s%%)", hsvData);
			String hex = format("#%02X%02X%02X", rgbInfo);

			//Set display
			String[] dynamicLabelText = {pos, rgb, hsv, hex};
			display.setDynamicLabelText(dynamicLabelText);
			display.setPanelColor(pixelColor);
			display.setPosition(mousePosition);
			display.copyIfRequested();
		}
	}

	/**
	 * Convert RGB values into HSV values.
	 *
	 * @param rgb
	 *            An array of RGB values
	 * @return An array of HSV values
	 */
	private int[] extractHSV(int[] rgb)
	{
		//Convert RGB to HSV
		int[] hsv = new int[rgb.length];
		float[] hsb = new float[rgb.length];
		Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);

		//Convert to percentages
		for (int i = 0; i < hsv.length; i++)
			hsv[i] = Math.round(hsb[i] * 100);

		return hsv;
	}

	/**
	 * Format pixel data to be displayed.
	 *
	 * @param format
	 *            The format string to be applied to the data
	 * @param data
	 *            The int array to be formatted
	 * @return The formatted string
	 */
	private String format(String format, int[] data)
	{
		//Convert primitive ints to Integers to prepare for cast to Object array
		Integer[] dataAsObj = new Integer[data.length];

		for (int i = 0; i < data.length; i++)
			dataAsObj[i] = data[i];

		return String.format(format, (Object[]) dataAsObj);
	}
}