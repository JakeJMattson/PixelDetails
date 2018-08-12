package io.github.JakeJMattson.pixeldetails;

		import java.awt.*;
		import java.util.*;

		import javax.swing.*;

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
	private void setup()
	{
		//Create robot to get pixel color
		Robot robot = createRobot();

		if (robot == null)
			return;

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
		ActionPanel[] panels = createPanels(infoChoices);
		DetailDisplay display = new DetailDisplay(panels, isDynamic, shouldCopyLabels);

		while (display.isOpen())
		{
			display.updateComponents(robot);
			display.copyIfRequested();
		}
	}

	/**
	 * Allow the user to choose what data they want to see.
	 *
	 * @return A boolean array of user selections
	 */
	private static boolean[][] displayOptions()
	{
		//Prepare args
		OptionPanel info = new OptionPanel("Info to be displayed");
		info.addCheckBox("Coordinates", "Location (X,Y) of the mouse on the screen");
		info.addCheckBox("RGB", "Pixel color as 'Red, Green, Blue' values");
		info.addCheckBox("HSV", "Pixel color as 'Hue, Saturation, Value' values");
		info.addCheckBox("Hex", "Pixel color as Hexidecimal value");
		info.addCheckBox("Color bar", "Pixel color on a larger display");

		OptionPanel placement = new OptionPanel("Placement behavior");
		placement.addCheckBox("Dynamic placement", "Allow the frame to \"follow\" the mouse pointer");

		OptionPanel copy = new OptionPanel("Copy format");
		copy.addCheckBox("Include labels", "Static labels will be copied along with dynamic data");

		//Get choices from user
		Object[] components = {info, placement, copy};
		Object[] buttonText = {"Submit"};
		int choice = JOptionPane.showOptionDialog(null, components, "Display options", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);

		if (choice != JOptionPane.YES_OPTION)
			return null;

		boolean[][] selections = new boolean[3][];
		selections[0] = info.getSelections();
		selections[1] = placement.getSelections();
		selections[2] = copy.getSelections();

		return selections;
	}

	private ActionPanel[] createPanels(boolean[] selections)
	{
		ArrayList<ActionPanel> panels = new ArrayList<>();

		if (selections[0])
		{
			Action action = (pixelColor, mouse) ->
			{
				int[] coordinates = {mouse.x, mouse.y};
				return format("(%s, %s)", coordinates);
			};

			panels.add(new ActionPanel("X,Y = ", action));
		}
		if (selections[1])
		{
			Action action = (pixelColor, mouse) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				return format("(%s, %s, %s)", rgbInfo);
			};

			panels.add(new ActionPanel("RGB = ", action));

		}
		if (selections[2])
		{
			Action action = (pixelColor, mouse) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				int[] hsvData = extractHSV(rgbInfo);
				return format("(%s%%, %s%%, %s%%)", hsvData);
			};

			panels.add(new ActionPanel("HSV = ", action));
		}
		if (selections[3])
		{
			Action action = (pixelColor, mouse) ->
			{
				int[] rgbInfo = {pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue()};
				return format("#%02X%02X%02X", rgbInfo);
			};

			panels.add(new ActionPanel("Hex = ", action));
		}

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
		//Convert primitive ints to Integers to prepare for cast to Object array
		Integer[] dataAsObj = new Integer[data.length];

		for (int i = 0; i < data.length; i++)
			dataAsObj[i] = data[i];

		return String.format(format, (Object[]) dataAsObj);
	}

	//Interface used for lambda expressions
	public interface Action
	{
		String performAction(Color pixelColor, Point mouse);
	}
}