package io.github.JakeJMattson.pixeldetails;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SectionPanel extends JPanel
{
	public SectionPanel(String title)
	{
		//Create panel
		super();

		//Set look and feel
		this.setLayout(new GridLayout(0, 1));
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title,
				TitledBorder.LEFT, TitledBorder.TOP));
	}

	public void addCheckBox(String boxText, String tooltip)
	{
		//Create check box
		JCheckBox box = new JCheckBox(boxText);
		box.setSelected(true);
		box.setToolTipText(tooltip);

		//Add box to panel
		this.add(box);
	}

	public boolean[] getSelections()
	{
		//Get components from panel
		Component[] checkBoxes = this.getComponents();

		//Collect selection status from all check boxes
		boolean[] selections = new boolean[checkBoxes.length];

		for (int i = 0; i < checkBoxes.length; i++)
			selections[i] = ((JCheckBox) checkBoxes[i]).isSelected();

		return selections;
	}
}