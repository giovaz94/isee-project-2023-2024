package io.github.evasim.view

import io.kotest.core.spec.style.FreeSpec
import javafx.application.Platform
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UITest : FreeSpec({

    beforeSpec {
        // Headless JavaFX setup
        System.setProperty("testfx.robot", "glass")
        System.setProperty("testfx.headless", "true")
        System.setProperty("glass.platform", "Monocle")
        System.setProperty("monocle.platform", "Headless")
        System.setProperty("java.awt.headless", "true")
        Platform.setImplicitExit(true) // allows JavaFX to exit when all windows are closed
        // Ensure JavaFX thread is correctly initialized
        val startupLatch = CountDownLatch(1)
        Platform.startup(startupLatch::countDown)
        startupLatch.await(5, TimeUnit.SECONDS)
    }

    "JavaFX UI launches without errors" {
        runBlocking {
            val view = FXSimulatorView()
//            val viewThread = view.start()
            view.start()
            // Terminates the JavaFX application
            Platform.runLater {
                javafx.stage.Window.getWindows().filterIsInstance<javafx.stage.Stage>().forEach { it.close() }
                Platform.exit()
            }
//            viewThread.join(5.seconds.inWholeMilliseconds)
//            if (viewThread.isAlive) {
//                viewThread.interrupt()
//                fail("Test is stuck. The view thread did not terminate.")
//            }
        }
    }
})
