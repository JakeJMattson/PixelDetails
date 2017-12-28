import java.awt.*;
import javax.swing.JOptionPane;

public class ColorGrabber
{
	public static void main(String[] args)
	{
		ColorGrabber driver = new ColorGrabber();

		try
		{
			driver.start();
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.toString(), "Something went wrong!", JOptionPane.ERROR_MESSAGE);
		}
		finally
		{
			System.exit(0);
		}
	}

	public void start()
	{
		//Local variables
		ColorDisplay display = new ColorDisplay();
		Robot robot = createRobot();

		//While frame is open
		while (display.getStatus())
		{
			//Get base info
			Point mousePosition = getMousePosition();
			Color currentColor = getCurrentColor(robot, mousePosition);
			int[] rgbInfo = getColorInfo(currentColor);

			//Create strings to display
			String coordinates = createCoordinateString(mousePosition);
			String rgb = createRGBString(rgbInfo);
			String hsv = createHSVString(rgbInfo);
			String hex = createHexString(rgbInfo);

			//Set display
			display.setText(coordinates, rgb, hsv, hex);
			display.setColor(currentColor);
			display.setPosition(mousePosition);
		}
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

	private Point getMousePosition()
	{
		return MouseInfo.getPointerInfo().getLocation();
	}

	private Color getCurrentColor(Robot robot, Point mousePosition)
	{
		return robot.getPixelColor(mousePosition.x, mousePosition.y);
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