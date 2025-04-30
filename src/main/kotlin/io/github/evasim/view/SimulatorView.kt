package io.github.evasim.view

import io.github.evasim.controller.SimulatorController

/** The UI simualator interface. */
interface SimulatorView {

    /** The controller of the simulator. */
    var controller: SimulatorController?

    /** Start the simulator view. */
    fun start()
}
