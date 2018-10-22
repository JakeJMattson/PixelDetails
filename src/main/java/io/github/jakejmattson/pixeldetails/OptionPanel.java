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
import javax.swing.border.TitledBorder;
import java.awt.*;

class OptionPanel extends JPanel
{
	OptionPanel(String title)
	{
		super(new GridLayout(0, 1));
		super.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title,
				TitledBorder.LEFT, TitledBorder.TOP));
	}

	void addCheckBox(String boxText, String tooltip)
	{
		JCheckBox box = new JCheckBox(boxText);
		box.setSelected(true);
		box.setToolTipText(tooltip);

		this.add(box);
	}

	boolean[] getSelections()
	{
		Component[] checkBoxes = this.getComponents();
		boolean[] selections = new boolean[checkBoxes.length];

		for (int i = 0; i < checkBoxes.length; i++)
			selections[i] = ((JCheckBox) checkBoxes[i]).isSelected();

		return selections;
	}
}