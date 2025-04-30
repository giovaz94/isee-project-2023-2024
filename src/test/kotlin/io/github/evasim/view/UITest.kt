package io.github.evasim.view

import io.github.evasim.controller.SimulatorController
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import io.mockk.mockk
import javafx.application.Platform
import kotlinx.coroutines.delay
import java.time.Duration
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
        val view = FXSimulatorView().also { it.controller = mockk<SimulatorController>() }
        val viewThread = thread(start = true) {
            view.start() // this blocks until the view is closed
        }
        delay(2.seconds) // let the UI start
        // Terminates the JavaFX application
        Platform.runLater {
            javafx.stage.Window.getWindows().filterIsInstance<javafx.stage.Stage>().forEach { it.close() }
        }
        viewThread.join(Duration.ofSeconds(5))
        if (viewThread.isAlive) {
            viewThread.interrupt()
            fail("Test is stuck. The view thread did not terminate.")
        }
    }

    afterSpec {
        runCatching { Platform.exit() }
    }
})
