package io.github.evasim.view

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import javafx.application.Platform
import kotlinx.coroutines.delay
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.seconds

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
        val view = FXSimulatorView()
        val viewThread = thread {
            view.launch()
        }
        delay(2.seconds)
        // Terminates the JavaFX application
        Platform.runLater {
            javafx.stage.Window.getWindows().filterIsInstance<javafx.stage.Stage>().forEach { it.close() }
            Platform.exit()
        }
        viewThread.join(10.seconds.inWholeMilliseconds)
        if (viewThread.isAlive) {
            viewThread.interrupt()
            fail("Test is stuck. The view thread did not terminate.")
        }
    }
})
