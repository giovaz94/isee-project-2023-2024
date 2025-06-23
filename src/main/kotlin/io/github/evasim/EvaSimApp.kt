package io.github.evasim

import io.github.evasim.view.FXSimulatorView

/** EvaSim Application Entry Point object. */
object EvaSimApp {

    /** Main method to start the EvaSim application. */
    @JvmStatic
    fun main(args: Array<String>) = FXSimulatorView().launch()
}
