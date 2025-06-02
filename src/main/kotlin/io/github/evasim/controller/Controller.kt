package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
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

    private var domain: World? = null

    @Synchronized
    override fun start(configuration: Configuration) {
        require(domain == null) { "A simulation is already running. Please, stop it first." }
        World.fromConfiguration(configuration).also { world ->
            domain = world
            startMas(world)
            subscribers.forEach { world.register(it) }
        }
        post(UpdatedWorld(domain ?: error("Cannot render a non-existing simulation!")))
    }

    // TODO: think if this is the right place where to start the agents, e.g., in the rounds manager.
    private fun startMas(domain: Domain) = mas {
        environment(SimulationEnvironment(domain))
        domain.blobs.forEach { blobAgent(it) }
        executionStrategy = ExecutionStrategy.discreteTimeExecution()
    }.let { thread { it.start() } }

    @Synchronized
    override fun stop() {
        requireNotNull(domain) { "Cannot stop a non-existing simulation!" }
        domain = null
    }
}
