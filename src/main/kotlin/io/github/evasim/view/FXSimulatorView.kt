package io.github.evasim.view

import io.github.evasim.controller.Boundary
import io.github.evasim.controller.SimulatorController
import io.github.evasim.utils.resource
import javafx.application.Application
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

/** JavaFX implementation of the simulator view. */
class FXSimulatorView : Application(), Boundary {

    override fun start(primaryStage: Stage) {
        val fxmlFile = resource(LAYOUT_FILE)
        val styleFile = resource(STYLE_FILE)
        val fxmlLoader = FXMLLoader(fxmlFile)
        primaryStage.apply {
            title = "EvaSim Simulator"
            scene = Scene(fxmlLoader.load())
            scene.stylesheets.add(styleFile.toExternalForm())
            isMaximized = true
            onCloseRequest = EventHandler {
                it.consume() // Prevent default close action
                SimulatorController.stop()
                primaryStage.close() // Close the stage explicitly
            }
            show()
        }
    }

    override fun launch() {
        launch(this@FXSimulatorView.javaClass)
    }

    private companion object {
        private const val LAYOUT_FILE = "ui/layouts/Main.fxml"
        private const val STYLE_FILE = "ui/css/style.css"
    }
}
