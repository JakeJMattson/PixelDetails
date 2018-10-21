/*
 * The MIT License
 * Copyright © 2017 Jake Mattson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.jakejmattson.pixeldetails;

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

	String getText()
	{
		return dynamicLabel.getText();
	}

	void performAction(Point mousePosition, Color pixelColor)
	{
		dynamicLabel.setText(action.performAction(mousePosition, pixelColor));
	}
}