package io.github.evasim.view

import io.github.evasim.controller.Boundary
import io.github.evasim.view.utils.resource
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.concurrent.thread

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
class FXSimulatorView(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : Application(), Boundary {

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
        FXAppContext.viewReady.complete(this)
    }

    override suspend fun start() {
        withContext(dispatcher) {
            thread {
                launch(this@FXSimulatorView.javaClass)
            }
        }
        FXAppContext.viewReady.await()
    }

    private companion object {
        private const val LAYOUT_FILE = "ui/layouts/MainWindow.fxml"
        private const val STYLE_FILE = "ui/css/style.css"
    }
}
