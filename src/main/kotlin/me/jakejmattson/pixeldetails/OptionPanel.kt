package me.jakejmattson.pixeldetails

import java.awt.*
import javax.swing.*
import javax.swing.border.TitledBorder

internal class OptionPanel(title: String) : JPanel(GridLayout(0, 1)) {
    val selections: BooleanArray
        get() = components.map { (it as JCheckBox).isSelected }.toBooleanArray()

    init {
        border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title, TitledBorder.LEFT, TitledBorder.TOP)
    }

    fun firstSelection() = selections.first()

    fun addCheckBox(boxText: String, tooltip: String) = add(JCheckBox(boxText).apply {
        isSelected = true
        toolTipText = tooltip
    })
}