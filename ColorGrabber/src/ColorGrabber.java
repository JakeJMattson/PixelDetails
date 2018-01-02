import java.awt.*;
import javax.swing.*;

public class ColorGrabber
{
	public static void main(String[] args)
	{		
		try
		{			
			//Begin program
			ColorGrabber driver = new ColorGrabber();
			driver.start();
		}
		catch (Exception e)
		{
			//Report fatal errors to user
			JOptionPane.showMessageDialog(null, e.toString(), "Something went wrong!", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			//Terminate program
			System.exit(0);
		}
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
			System.exit(0);
		
		//Allow user to choose placement options
		boolean dynamic = JOptionPane.showConfirmDialog(null, "Would you like the display to be dynamically placed near the mouse pointer?",
														"Display options", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		
		//Create robot to get pixel color
		Robot robot = createRobot();
		
		//Create display
		String[] staticLabelText = {"X,Y = ", "RGB = ", "HSV = ", "Hex = "};
		ColorDisplay display = new ColorDisplay(staticLabelText, displayChoices, dynamic);	

		//While frame is open
		while (display.getStatus())
		{
			//Get base info
			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			Color currentColor = robot.getPixelColor(mousePosition.x, mousePosition.y);
			int[] rgbInfo = getColorInfo(currentColor);

			//Create strings to display
			String coordinates = createCoordinateString(mousePosition);
			String rgb = createRGBString(rgbInfo);
			String hsv = createHSVString(rgbInfo);
			String hex = createHexString(rgbInfo);

			//Set display
			String[] dynamicLabelText = {coordinates, rgb, hsv, hex};
			display.setDynamicLabelText(dynamicLabelText);
			display.setColor(currentColor);
			
			if (dynamic)
				display.setPosition(mousePosition);
		}
	}

	private static boolean[] displayOptions(String title, String[] options)
	{
		//Local variables
		boolean[] results = new boolean[options.length];
		JCheckBox[] boxes = new JCheckBox[options.length];
		Object[] buttonText = {"Submit"};
		
		//Create check boxes
		for (int i = 0; i < options.length; i++)
			boxes[i] = new JCheckBox(options[i]);
		
		//Get choices from user
		int choice = JOptionPane.showOptionDialog(null, boxes, title, JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, buttonText, buttonText[0]);
		
		for (int i = 0; i < results.length; i++)
			results[i] = boxes[i].isSelected();
		
		//Return results based on button press
		if (choice != JOptionPane.YES_OPTION)
			results = null;
		
		return results;
	}
	
	private Robot createRobot()
	{
		Robot robot = null;
		try
		{
			robot = new Robot();
		}
		catch (AWTException e)
		{
			e.printStackTrace();
		}

		return robot;
	}

	private int[] getColorInfo(Color pixelColor)
	{
		//Get rgb values from color
		int red = pixelColor.getRed();
		int green = pixelColor.getGreen();
		int blue = pixelColor.getBlue();

		//Create array of rgb values
		int[] rgb = {red, green, blue};

		return rgb;
	}
	
	private String createCoordinateString(Point mousePosition)
	{
		return String.format("(%s, %s)", mousePosition.x, mousePosition.y);
	}

	private String createRGBString(int[] rgb)
	{
		return String.format("(%s, %s, %s)", rgb[0], rgb[1], rgb[2]);
	}

	private String createHSVString(int[] rgb)
	{
		//Convert rgb to hsv
		float[] hsv = new float[3];
		Color.RGBtoHSB(rgb[0], rgb[1], rgb[2], hsv);

		//Convert to percentages
		int h = Math.round(hsv[0] * 100);
		int s = Math.round(hsv[1] * 100);
		int v = Math.round(hsv[2] * 100);

		return String.format("(%s%%, %s%%, %s%%)", h, s, v);
	}

	private String createHexString(int[] rgb)
	{
		return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
	}
}