package io.github.evasim.view.controllers

import javafx.scene.control.Button
import javafx.scene.layout.Pane

internal class ToggleController(private val button: Button, private val panel: Pane) {
    var isCollapsed = false

    init {
        button.text = "⟩"
        button.setOnAction { toggle() }
    }

    private fun toggle() {
        isCollapsed = !isCollapsed
        if (isCollapsed) {
            panel.styleClass.add("collapsed")
            button.text = "⟨"
        } else {
            panel.styleClass.remove("collapsed")
            button.text = "⟩"
        }
    }
}
