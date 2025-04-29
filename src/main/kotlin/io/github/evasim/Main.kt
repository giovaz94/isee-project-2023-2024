package io.github.evasim

import io.github.evasim.engine.SimulationEngine

/**
 * The entry point of the application.
 *
 * @param args an array of command-line arguments passed to the application.
 */
fun main(args: Array<String>) {
    val engine = SimulationEngine()
    engine.start()
}
