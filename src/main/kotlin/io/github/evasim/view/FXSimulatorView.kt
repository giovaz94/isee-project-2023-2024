package io.github.evasim.view

import io.github.evasim.controller.Domain
import io.github.evasim.controller.SimulatorController
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CompletableDeferred

/**
 * Represents the application context for the JavaFX-based simulator.
 *
 * This object provides global utilities and state management for the simulator application,
 * facilitating communication between asynchronous components during the application lifecycle.
 */
object FXAppContext {

    /**
     * Represents a deferred object used to signal when the JavaFX simulator view is ready for use.
     *
     * This `CompletableDeferred` instance is completed with an instance of `FXSimulatorView`
     * after the view has been launched and initialized. It provides a mechanism for asynchronous
     * coordination, allowing other components (such as the simulation controller) to await the
     * readiness of the view before proceeding with operations like setting the controller or rendering.
     */
    val viewReady = CompletableDeferred<FXSimulatorView>()
}

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
        println("Starting JavaFX application")
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
        FXAppContext.viewReady.complete(this)
    }

    override fun start() {
        requireNotNull(simulatorController) { "Controller must be set before starting the view." }
        launch()
        FXAppContext.viewReady.complete(this)
    }

    override fun render(domain: Domain) {
        viewController.render(domain)
    }

    private companion object {
        private const val LAYOUT_FILE = "/ui/layouts/MainWindow.fxml"
        private const val STYLE_FILE = "/ui/css/style.css"
    }
}
