package io.github.JakeJMattson.pixeldetails;

import javax.swing.*;
import java.awt.*;

class ActionPanel extends JPanel
{
	private String label;
	private JLabel dynamicLabel;
	private PixelReader.Action action;

	ActionPanel(String label, PixelReader.Action action)
	{
		super(new FlowLayout(FlowLayout.LEFT));

		this.label = label;
		this.action = action;

		createPanel();
	}

	private void createPanel()
	{
		//Create labels
		JLabel staticLabel = new JLabel(label);
		staticLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
		dynamicLabel = new JLabel();

		this.add(staticLabel);
		this.add(dynamicLabel);
	}

	String getLabel()
	{
		return label;
	}

	String getText()
	{
		return dynamicLabel.getText();
	}

	void performAction(Color pixelColor, Point mouse)
	{
		dynamicLabel.setText(action.performAction(pixelColor, mouse));
	}
}