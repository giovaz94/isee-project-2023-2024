package io.github.evasim.controller

/**
 * An interface representing a boundary in the simulation, i.e., a
 * pluggable system interface with external actors.
 */
interface Boundary {

    /** Starts the boundary. */
    suspend fun start()
}
