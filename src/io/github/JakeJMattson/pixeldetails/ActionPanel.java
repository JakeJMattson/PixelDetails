package io.github.JakeJMattson.pixeldetails;

import javax.swing.*;
import java.awt.*;

class ActionPanel extends JPanel
{
	private final String staticLabelText;
	private JLabel dynamicLabel;
	private final PixelReader.Action action;

	ActionPanel(String staticLabelText, PixelReader.Action action)
	{
		super(new FlowLayout(FlowLayout.LEFT));

		this.staticLabelText = staticLabelText;
		this.action = action;

		createPanel();
	}

	private void createPanel()
	{
		//Create labels
		JLabel staticLabel = new JLabel(staticLabelText);
		staticLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
		dynamicLabel = new JLabel();

		this.add(staticLabel);
		this.add(dynamicLabel);
	}

	String getStaticLabelText()
	{
		return staticLabelText;
	}

	String getText() { return dynamicLabel.getText(); }

	void performAction(Point mousePosition, Color pixelColor)
	{
		dynamicLabel.setText(action.performAction(mousePosition, pixelColor));
	}
}