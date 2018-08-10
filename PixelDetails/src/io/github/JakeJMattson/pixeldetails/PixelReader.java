package io.github.JakeJMattson.pixeldetails;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Performs pixel reading and formats data into strings.
 *
 * @author JakeJMattson
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
		//Get user choices
		boolean[][] displayChoices = displayOptions();

		//If submit button not clicked, exit program
		if (displayChoices == null)
			return;

		//Separate array
		boolean[] infoChoices = displayChoices[0];
		boolean isDynamic = displayChoices[1][0];
		boolean shouldCopyLabels = displayChoices[2][0];

		//Create display
		String[] staticLabelText = {"X,Y = ", "RGB = ", "HSV = ", "Hex = "};
		DetailDisplay display = new DetailDisplay(staticLabelText, infoChoices, isDynamic, shouldCopyLabels);

		//Start program
		start(display);
	}

	/**
	 * Allow the user to choose what data they want to see.
	 *
	 * @return A boolean array of user selections
	 */
	private static boolean[][] displayOptions()
	{
		//Prepare args
		SectionPanel info = new SectionPanel("Info to be displayed");
		info.addCheckBox("Coordinates", "Location (X,Y) of the mouse on the screen");
		info.addCheckBox("RGB", "Pixel color as 'Red, Green, Blue' values");
		info.addCheckBox("HSV", "Pixel color as 'Hue, Saturation, Value' values");
		info.addCheckBox("Hex", "Pixel color as Hexidecimal value");
		info.addCheckBox("Color bar", "Pixel color on a larger display");

		SectionPanel placement = new SectionPanel("Placement behavior");
		placement.addCheckBox("Dynamic placement", "Allow the frame to \"follow\" the mouse pointer");

		SectionPanel copy = new SectionPanel("Copy format");
		copy.addCheckBox("Include labels", "Static labels will be copied along with dynamic data");

		//Get choices from user
		Object[] components = {info, placement, copy};
		Object[] buttonText = {"Submit"};
		int choice = JOptionPane.showOptionDialog(null, components, "Display options", JOptionPane.PLAIN_MESSAGE,
				JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);

		if (choice != JOptionPane.YES_OPTION)
			return null;

		boolean[][] selections = new boolean[3][];
		selections[0] = info.getSelections();
		selections[1] = placement.getSelections();
		selections[2] = copy.getSelections();

		if (!containsTrue(selections[0]))
		{
			//Exit if no info boxes were selected
			selections = null;
			JOptionPane.showMessageDialog(null, "No info requested. Terminating.", "Error!",
					JOptionPane.ERROR_MESSAGE);
		}

		return selections;
	}

	private static boolean containsTrue(boolean[] boolArray)
	{
		for (boolean value: boolArray)
			if(value)
				return true;

		return false;
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

		if (robot == null)
			return;

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
			JOptionPane.showMessageDialog(null, "Failed to create Robot.", "Internal Error!",
					JOptionPane.ERROR_MESSAGE);

			return null;
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