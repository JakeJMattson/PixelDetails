package io.github.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder

internal class OptionPanel(title: String): JPanel(GridLayout(0, 1)) {

    val selections: BooleanArray
        get() = this.components.map { (it as JCheckBox).isSelected }.toBooleanArray()

    init {
        border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK), title, TitledBorder.LEFT, TitledBorder.TOP)
    }

    fun firstSelection() = selections.first()

    fun addCheckBox(boxText: String, tooltip: String): OptionPanel {
        this.add(JCheckBox(boxText).apply { isSelected = true; toolTipText = tooltip })
        return this
    }
}