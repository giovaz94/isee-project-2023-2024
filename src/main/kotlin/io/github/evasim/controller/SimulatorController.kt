package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
import io.github.evasim.model.EventBusPublisher
import io.github.evasim.model.EventPublisher
import io.github.evasim.model.Round
import io.github.evasim.model.World
import io.github.evasim.utils.logger
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import kotlin.concurrent.thread

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
            // val initialRound = Round.byNoFood(world)
            // TODO: just for testing until we have a proper contention in place
            val initialRound = Round.byCriteria(world) {
                it.foods.all { food -> !food.hasUncollectedPieces() }
            }
            activeSimulation = thread { simulationLoop(initialRound) }
        }
    }

    private fun simulationLoop(round: Round, shouldStop: (Round) -> Boolean = { false }) {
        logger.info { "Starting round ${round.number}..." }
        subscribers.forEach { round.world.register(it) }
        mas(round).start()
        logger.info { "Round ${round.number} ended." }
        if (shouldStop(round)) return else simulationLoop(round.next())
    }

    private fun mas(round: Round) = mas {
        executionStrategy = ExecutionStrategy.discreteTimeExecution()
        round.world.blobs.forEach { blobAgent(it) }
        environment(SimulationEnvironment(round))
    }

    @Synchronized
    override fun stop() {
        checkNotNull(activeSimulation) { "Cannot stop a non-existing simulation!" }
        activeSimulation = null
    }
}
