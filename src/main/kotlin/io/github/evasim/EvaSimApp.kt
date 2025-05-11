package io.github.evasim

import io.github.evasim.view.FXSimulatorView
import java.util.logging.Logger

/** EvaSim Application Entry Point object. */
object EvaSimApp {

    /** Main method to start the EvaSim application. */
    @JvmStatic
    fun main(args: Array<String>) {
        Logger.getLogger(javaClass.name).info("Starting EvaSim application...")
        FXSimulatorView().start()
    }
}
