package io.github.evasim.controller

import io.github.evasim.view.SimulatorView

/** The simulation controller. */
class SimulatorController(private val view: SimulatorView) {

    init {
        view.controller = this
        view.start()
    }
}
