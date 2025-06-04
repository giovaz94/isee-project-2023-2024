package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
import io.github.evasim.model.EventBusPublisher
import io.github.evasim.model.EventPublisher
import io.github.evasim.model.World
import io.github.evasim.model.World.Companion.Configuration
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import kotlin.concurrent.thread

/**
 * A type alias for the domain aggregate root of the simulation.
 */
typealias Domain = World

/**
 * Represents the simulation controller interface responsible for managing user inputs, events,
 * domain updates, and rendering.
 */
interface Controller : EventPublisher {

    /**
     * Starts the simulation with the given [configuration].
     */
    fun start(configuration: Configuration)

    /**
     * Stops the current simulation.
     */
    fun stop()
}

/** The simulation controller. */
object SimulatorController : Controller, EventBusPublisher() {

    private var environment: SimulationEnvironment? = null

    @Synchronized
    override fun start(configuration: Configuration) {
        require(environment == null) { "A simulation is already running. Please, stop it first." }
        World.fromConfiguration(configuration).also { world ->
            environment = startMas(world)
            subscribers.forEach { world.register(it) }
        }
    }

    // TODO: think if this is the right place where to start the agents, e.g., in the rounds manager.
    private fun startMas(domain: Domain) = SimulationEnvironment(domain).also {
        mas {
            executionStrategy = ExecutionStrategy.discreteTimeExecution()
            domain.blobs.forEach { blob -> blobAgent(blob) }
            environment(it)
        }.also { mas -> thread { mas.start() } }
    }

    @Synchronized
    override fun stop() {
        requireNotNull(environment) { "Cannot stop a non-existing simulation!" }
        environment?.updateData(mapOf("command" to "stop"))
        environment = null
    }
}
