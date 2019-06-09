package io.github.jakejmattson.pixeldetails

import org.jnativehook.*
import java.awt.*
import java.awt.event.*
import java.util.logging.*
import javax.swing.*

internal class DetailDisplay(private val panels: List<ActionPanel>,
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
            GlobalScreen.addNativeKeyListener(CopyKeyPressListener(panels, shouldCopyLabels))
        } catch (e: NativeHookException) {
            println("Failed to register Native Hook. Copying will be disabled.")
        }
    }

    private fun createWindowListener() = object: WindowAdapter() {
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
        panels.forEach {
            it.performAction(
                when (it) {
                    is CoordinatePanel -> mousePosition
                    is ColorPanel -> pixelColor
                    else -> pixelColor
                }
            )
        }

        if (colorPanel != null)
            colorPanel.background = pixelColor

        if (isDynamic) {
            setPosition(mousePosition)
            frame.pack()
        }
    }
}