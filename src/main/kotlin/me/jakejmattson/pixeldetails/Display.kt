package me.jakejmattson.pixeldetails

import org.jnativehook.*
import java.awt.*
import java.awt.event.*
import java.util.logging.*
import javax.swing.*
import javax.swing.border.TitledBorder

internal class Display(private val panels: List<ActionPanel>,
                       hasColorPanel: Boolean,
                       private val isDynamic: Boolean,
                       shouldCopyLabels: Boolean) {

    private val colorPanel: JPanel? = if (hasColorPanel) JPanel() else null
    private val frame = JFrame().apply {
        isAlwaysOnTop = true
        isUndecorated = isDynamic
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
        isVisible = true
        addWindowListener(createWindowListener())
        add(createDisplayPanel())
        pack()

        if (!isDynamic)
            setSize((width * 1.4).toInt(), height)
    }
    var isOpen = true
        private set

    init {
        try {
            Logger.getLogger(GlobalScreen::class.java.getPackage().name).apply { level = Level.WARNING; useParentHandlers = false }
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(CopyListener(panels, shouldCopyLabels))
        } catch (e: NativeHookException) {
            println("Failed to register Native Hook. Copying will be disabled.")
        }
    }

    private fun createWindowListener() = object : WindowAdapter() {
        override fun windowClosing(windowClosed: WindowEvent?) {
            isOpen = false
        }
    }

    private fun createDisplayPanel() = JPanel(GridLayout(0, 1)).apply {
        panels.forEach { add(it) }

        if (colorPanel != null)
            add(colorPanel)

        if (isDynamic)
            border = BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK)
    }

    private fun setPosition(framePosition: Point) {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val frameHeight = frame.bounds.height
        val frameWidth = frame.bounds.width
        val buffer = 10

        with(framePosition) {
            val rightBound = framePosition.getX() + frameWidth
            val lowerBound = framePosition.getY() + frameHeight

            if (rightBound >= screenSize.getWidth())
                x -= frameWidth + buffer
            else
                x += buffer

            if (lowerBound >= screenSize.getHeight())
                y -= frameHeight + buffer
            else
                y += buffer
        }

        frame.location = framePosition
    }

    fun updateComponents(mousePosition: Point, pixelColor: Color) {
        val pixel = Pixel(mousePosition, pixelColor)

        panels.forEach {
            it.performAction(pixel)
        }

        if (colorPanel != null)
            colorPanel.background = pixelColor

        if (isDynamic) {
            setPosition(mousePosition)
            frame.pack()
        }
    }
}

class ActionPanel(val labelText: String, private val action: (Pixel) -> String) : JPanel(FlowLayout(FlowLayout.LEFT)) {
    private val dynamicLabel = JLabel()

    var text: String
        get() = dynamicLabel.text
        set(value) {
            dynamicLabel.text = value
        }

    init {
        this.add(JLabel(labelText).apply { font = Font("Monospaced", Font.BOLD, 12) })
        this.add(dynamicLabel)
    }

    fun performAction(pixel: Pixel) {
        text = action.invoke(pixel)
    }
}

class OptionPanel(title: String, builder: OptionPanel.() -> Unit) : JPanel(GridLayout(0, 1)) {
    private val options = mutableListOf<JCheckBox>()

    val selections
        get() = options.map { it.isSelected }

    init {
        border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), title, TitledBorder.LEFT, TitledBorder.TOP)
        builder.invoke(this)

        options.forEach {
            this.add(it)
        }
    }

    operator fun get(index: Int) = options[index].isSelected

    fun option(boxText: String, tooltip: String) {
        options.add(
            JCheckBox(boxText).apply {
                isSelected = true
                toolTipText = tooltip
            }
        )
    }
}