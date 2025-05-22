package io.github.evasim.view

import io.github.evasim.controller.Boundary
import io.github.evasim.utils.resource
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL

/** JavaFX implementation of the simulator view. */
class FXSimulatorView : Application(), Boundary {

    override fun start(primaryStage: Stage) {
        val fxmlFile: URL = resource(LAYOUT_FILE)
        val styleFile = resource(STYLE_FILE)
        val fxmlLoader = FXMLLoader(fxmlFile)
        primaryStage.apply {
            title = "EvaSim Simulator"
            scene = Scene(fxmlLoader.load())
            scene.stylesheets.add(styleFile.toExternalForm())
            show()
        }
    }

    override fun launch() {
        launch(this@FXSimulatorView.javaClass)
    }

    private companion object {
        private const val LAYOUT_FILE = "ui/layouts/MainWindow.fxml"
        private const val STYLE_FILE = "ui/css/style.css"
    }
}
