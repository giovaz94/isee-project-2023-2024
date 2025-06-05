package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
import io.github.evasim.model.EventBusPublisher
import io.github.evasim.model.EventPublisher
import io.github.evasim.model.Round
import io.github.evasim.model.World
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
    fun start(configuration: World.Companion.Configuration)

    /**
     * Stops the current simulation.
     */
    fun stop()
}

/** The simulation controller. */
object SimulatorController : Controller, EventBusPublisher() {

    private var activeSimulation: Thread? = null

    @Synchronized
    override fun start(configuration: World.Companion.Configuration) {
        check(activeSimulation == null) { "A simulation is already running. Please, stop it first." }
        World.fromConfiguration(configuration).also { world ->
            // TODO: make it configurable the round and simulation end criteria
            val initialRound = Round.byEmptyWorld(world)
            activeSimulation = thread { simulationLoop(initialRound) }
            subscribers.forEach { world.register(it) }
        }
    }

    private fun simulationLoop(round: Round, shouldStop: (Round) -> Boolean = { false }) {
        mas(round).start()
        if (shouldStop(round)) return else simulationLoop(round.next())
    }

    private fun mas(round: Round) = SimulationEnvironment(round).let {
        mas {
            executionStrategy = ExecutionStrategy.discreteTimeExecution()
            round.world.blobs.forEach { blobAgent(it) }
            environment(it)
        }
    }

    @Synchronized
    override fun stop() {
        checkNotNull(activeSimulation) { "Cannot stop a non-existing simulation!" }
        activeSimulation = null
    }
}
