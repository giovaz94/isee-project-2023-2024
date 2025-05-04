package io.github.evasim.view

import io.github.evasim.controller.Domain
import io.github.evasim.controller.SimulatorController

/** The UI simualator interface. */
interface SimulatorView {

    /** The controller of the simulator. */
    var controller: SimulatorController?

    /** Start the simulator view. */
    fun start()

    /**
     * Renders the current state of the simulation domain in the simulator view.
     *
     * @param domain The domain representing the current state of the simulation,
     *               containing entities and their properties to be displayed.
     */
    fun render(domain: Domain)
}
