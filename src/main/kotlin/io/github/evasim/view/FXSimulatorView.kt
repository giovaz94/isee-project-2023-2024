package io.github.evasim.view

import io.github.evasim.controller.SimulatorController
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.text.Font
import javafx.stage.Stage

/** JavaFX implementation of the simulator view. */
class FXSimulatorView : Application(), SimulatorView {
    private var simulatorController: SimulatorController? = null

    override var controller: SimulatorController?
        get() = simulatorController
        set(value) {
            simulatorController = value
        }

    override fun start(primaryStage: Stage) {
        val message = Label("Hello, JavaFX!")
        message.font = Font(100.0)
        primaryStage.setScene(Scene(message))
        primaryStage.title = "Hello"
        primaryStage.show()
    }

    override fun start() {
        requireNotNull(simulatorController) { "Controller must be set before starting the view." }
        launch()
    }
}
