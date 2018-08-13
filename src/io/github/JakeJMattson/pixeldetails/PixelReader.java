package io.github.JakeJMattson.pixeldetails;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * Performs pixel reading and formats data into strings.
 *
 * @author JakeJMattson
 */
class PixelReader
{
	public static void main(String[] args)
	{
		PixelReader driver = new PixelReader();
		driver.setup();
	}

	/**
	 * Create all necessary components and start the program.
	 */
	private void setup()
	{
		//Create robot to get pixel color
		Robot robot = createRobot();

		if (robot == null)
			return;

		//Prepare args
		OptionPanel info = new OptionPanel("Info to be displayed (Text)");
		info.addCheckBox("Coordinates", "Location (X,Y) of the mouse on the screen");
		info.addCheckBox("RGB", "Pixel color as 'Red, Green, Blue' values");
		info.addCheckBox("HSV", "Pixel color as 'Hue, Saturation, Value' values");
		info.addCheckBox("Hex", "Pixel color as Hexadecimal value");

		OptionPanel color = new OptionPanel("Color panel");
		color.addCheckBox("Color panel", "Pixel color on a larger display");

		OptionPanel placement = new OptionPanel("Placement behavior");
		placement.addCheckBox("Dynamic placement", "Allow the frame to \"follow\" the mouse pointer");

		OptionPanel copy = new OptionPanel("Copy format");
		copy.addCheckBox("Include labels", "Static labels will be copied along with dynamic data");

		//If submit button not clicked, exit program
		if (!displayOptions(info, color, placement, copy))
			return;

		//Get user selections
		boolean[] infoChoices = info.getSelections();
		boolean hasColorPanel = color.getSelections()[0];
		boolean isDynamic = placement.getSelections()[0];
		boolean shouldCopyLabels = copy.getSelections()[0];

		//Create display
		ActionPanel[] panels = createPanels(infoChoices);

		if (panels.length == 0)
			return;

		DetailDisplay display = new DetailDisplay(panels, hasColorPanel, isDynamic, shouldCopyLabels);

		while (display.isOpen())
		{
			//Update info based on mouse location
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			Color pixelColor = robot.getPixelColor(mousePosition.x, mousePosition.y);

			display.updateComponents(mousePosition, pixelColor);
			display.copyIfRequested();
		}
	}

	/**
	 * Allow the user to choose what data they want to see.
	 *
	 * @return A boolean array of user selections
	 */
	private boolean displayOptions(OptionPanel... options)
	{
		//Get choices from user
		Object[] buttonText = {"Submit"};
		int choice = JOptionPane.showOptionDialog(null, options, "Display options", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);

		return choice == JOptionPane.YES_OPTION;
	}

	/**
	 * Create an array of panels that will self-populate when updated with mouse information.
	 *
	 * @param selections Panels to be created (selected by user)
	 * @return Array of created panels
	 */
	private ActionPanel[] createPanels(boolean[] selections)
	{
		ArrayList<ActionPanel> panels = new ArrayList<>();

		//Mouse Coordinates
		if (selections[0])
			panels.add(new ActionPanel("X,Y = ", (mouse, pixelColor) ->
			{
				int[] coordinates = {mouse.x, mouse.y};
				return format("(%s, %s)", coordinates);
			}));

		//RGB values
		if (selections[1])
			panels.add(new ActionPanel("RGB = ", (mouse, pixelColor) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				return format("(%s, %s, %s)", rgbInfo);
			}));

		//HSV values
		if (selections[2])
			panels.add(new ActionPanel("HSV = ", (mouse, pixelColor) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				int[] hsvData = extractHSV(rgbInfo);
				return format("(%s%%, %s%%, %s%%)", hsvData);
			}));

		//Hex value
		if (selections[3])
			panels.add(new ActionPanel("Hex = ", (mouse, pixelColor) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				return format("#%02X%02X%02X", rgbInfo);
			}));

		return panels.toArray(new ActionPanel[]{});
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
		Object[] dataAsObj = new Integer[data.length];

		//Autobox ints to Integers
		for (int i = 0; i < data.length; i++)
			dataAsObj[i] = data[i];

		return String.format(format, dataAsObj);
	}

	/**
	 * Interface for lambda expressions
	 */
	public interface Action
	{
		String performAction(Point mouse, Color pixelColor);
	}
}