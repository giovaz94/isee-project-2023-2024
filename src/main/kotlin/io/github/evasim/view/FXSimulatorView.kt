package io.github.evasim.view

import io.github.evasim.controller.SimulatorController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage

/** JavaFX implementation of the simulator view. */
class FXSimulatorView : Application(), SimulatorView {
    private val viewController = FXSimulatorViewController()

    private var simulatorController: SimulatorController? = null

    override var controller: SimulatorController?
        get() = simulatorController
        set(value) {
            simulatorController = value
        }

    override fun start(primaryStage: Stage) {
        val fxmlFile = javaClass.getResource(LAYOUT_FILE) ?: error("Could not find fxml file")
        val styleFile = javaClass.getResource(STYLE_FILE) ?: error("Could not find style file")
        val fxmlLoader = FXMLLoader(fxmlFile)
        fxmlLoader.setController(viewController)
        primaryStage.apply {
            title = "EvaSim Simulator"
            scene = Scene(fxmlLoader.load())
            scene.stylesheets.add(styleFile.toExternalForm())
            show()
        }
    }

    override fun start() {
        requireNotNull(simulatorController) { "Controller must be set before starting the view." }
        launch()
    }

    private companion object {
        private const val LAYOUT_FILE = "/ui/layouts/MainWindow.fxml"
        private const val STYLE_FILE = "/ui/css/style.css"
    }
}
