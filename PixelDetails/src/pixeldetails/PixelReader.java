/*
 * Project Description:
 * This project is designed to give various pixel information at the current mouse point.
 */

package pixeldetails;

import java.awt.*;

import javax.swing.*;

/**
 * Performs pixel reading and formats data into strings
 *
 * @author mattson543
 */
public class PixelReader
{
	public static void main(String[] args)
	{
		try
		{
			PixelReader driver = new PixelReader();
			driver.setup();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to create Robot. Please try again.", "Internal Error!",
					JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			System.exit(0);
		}
	}

	/**
	 * Create all necessary components and start the program
	 *
	 * @throws AWTException
	 *             In the event that the creation of the robot fails
	 */
	public void setup() throws AWTException
	{
		//Allow user to choose display options
		String title = "Select desired info to display";
		String[] options = {"Coordinates", "RGB", "HSV", "Hex", "Color bar"};

		//Get user choices
		boolean[] displayChoices = displayOptions(title, options);

		//If submit button not clicked, exit program
		if (displayChoices == null)
			return;

		//Allow user to choose placement options
		boolean dynamic = JOptionPane.showConfirmDialog(null,
				"Would you like the display to be dynamically placed near the mouse pointer?",
				"Display options", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

		//Create display
		String[] staticLabelText = {"X,Y = ", "RGB = ", "HSV = ", "Hex = "};
		DetailDisplay display = new DetailDisplay(staticLabelText, displayChoices, dynamic);

		//Create robot to get pixel color
		Robot robot = new Robot();

		//Start program
		start(display, robot);
	}

	/**
	 * Gets pixel info, formats it, then sends all information to the display
	 *
	 * @param display
	 *            The display frame that is shown to the user
	 * @param robot
	 *            The Java Robot to get the pixel information
	 */
	private void start(DetailDisplay display, Robot robot)
	{
		while (display.isOpen())
		{
			//Get base info
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			Color pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y);
			int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
			int[] coord = {mousePosition.x, mousePosition.y};
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
		}
	}

	/**
	 * Allow the user to choose what data they want to see
	 *
	 * @param title
	 *            The title displayed at the top of the option pane
	 * @param options
	 *            The array of choices to be displayed to the user
	 * @return A boolean array of user selections
	 */
	private static boolean[] displayOptions(String title, String[] options)
	{
		//Local variables
		JCheckBox[] boxes = new JCheckBox[options.length];

		//Create check boxes
		for (int i = 0; i < options.length; i++)
		{
			//Create new check box and check it
			boxes[i] = new JCheckBox(options[i]);
			boxes[i].setSelected(true);
		}

		//Get choices from user
		Object[] buttonText = {"Submit"};
		int choice = JOptionPane.showOptionDialog(null, boxes, title, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);

		//Return results based on button press
		boolean[] selections = null;

		if (choice == JOptionPane.YES_OPTION)
		{
			selections = new boolean[options.length];

			for (int i = 0; i < boxes.length; i++)
				selections[i] = boxes[i].isSelected();
		}

		return selections;
	}

	/**
	 * Convert the given RGB values into HSV values
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
	 * Format pixel data to be displayed
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