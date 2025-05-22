package io.github.evasim

import io.github.evasim.utils.logger
import io.github.evasim.view.FXSimulatorView
import kotlinx.coroutines.runBlocking

/** EvaSim Application Entry Point object. */
object EvaSimApp {

    /** Main method to start the EvaSim application. */
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        logger.info("Starting EvaSim application...")
        FXSimulatorView().launch()
    }
}
