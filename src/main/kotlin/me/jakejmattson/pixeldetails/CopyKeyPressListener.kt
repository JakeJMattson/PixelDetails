package me.jakejmattson.pixeldetails

import org.jnativehook.keyboard.*
import org.jnativehook.keyboard.NativeKeyEvent.*
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

internal class CopyKeyPressListener(private val panels: List<ActionPanel>, private val shouldCopyLabels: Boolean): NativeKeyListener {
    private var keyList: ArrayList<Int> = ArrayList()

    override fun nativeKeyPressed(event: NativeKeyEvent) {
        keyList.add(event.keyCode)

        if (VC_CONTROL in keyList && VC_C in keyList)
            copy()
    }

    override fun nativeKeyReleased(event: NativeKeyEvent) {
        keyList.remove(event.keyCode)
    }

    override fun nativeKeyTyped(event: NativeKeyEvent) = Unit

    private fun copy() = Toolkit.getDefaultToolkit().systemClipboard.setContents(
        StringSelection(buildString {
            panels.forEach { panel ->
                if (shouldCopyLabels)
                    append(panel.labelText)

                appendln(panel.text)
            }
        }), null)
}