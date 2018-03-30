/*
 * Project Description:
 * This project is designed to give various pixel information at the current mouse point.
 *
 * Class Description:
 * Performs pixel reading and formats data into strings
 */

package pixeldetails;

import java.awt.*;

import javax.swing.*;

public class PixelReader
{
	public static void main(String[] args)
	{
		//Begin program
		PixelReader driver = new PixelReader();
		driver.start();

		//Terminate program after window closes
		System.exit(0);
	}

	public void start()
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

		//Create robot to get pixel color
		Robot robot = createRobot();

		//Create display
		String[] staticLabelText = {"X,Y = ", "RGB = ", "HSV = ", "Hex = "};
		DetailDisplay display = new DetailDisplay(staticLabelText, displayChoices, dynamic);

		//While frame is open
		while (display.getStatus())
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
			display.setColor(pixelColor);

			if (dynamic)
				display.setPosition(mousePosition);
		}
	}

	private static boolean[] displayOptions(String title, String[] options)
	{
		//Local variables
		JCheckBox[] boxes = new JCheckBox[options.length];

		//Create check boxes
		for (int i = 0; i < options.length; i++)
		{
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

	private Robot createRobot()
	{
		try
		{
			return new Robot();
		}
		catch (AWTException e)
		{
			return null;
		}
	}

	private int[] extractHSV(int[] rgb)
	{
		//Convert rgb to hsv
		int[] hsv = new int[rgb.length];
		float[] hsb = new float[rgb.length];
		Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsb);

		//Convert to percentages
		for (int i = 0; i < hsv.length; i++)
			hsv[i] = Math.round(hsb[i] * 100);

		return hsv;
	}

	private String format(String format, int[] data)
	{
		//Convert primitive ints to objects to prepare for cast to generic
		Integer[] dataAsObj = new Integer[data.length];

		for (int i = 0; i < data.length; i++)
			dataAsObj[i] = data[i];

		return String.format(format, (Object[]) dataAsObj);
	}
}