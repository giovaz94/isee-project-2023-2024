package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
import io.github.evasim.model.EventBusPublisher
import io.github.evasim.model.EventPublisher
import io.github.evasim.model.Round
import io.github.evasim.model.World
import io.github.evasim.utils.RandomConfig
import io.github.evasim.utils.Rnd
import io.github.evasim.utils.logger
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import kotlin.concurrent.thread
import kotlin.time.Duration

/**
 * Represents the simulation controller interface responsible for managing user inputs, events,
 * domain updates, and rendering.
 */
interface Controller : EventPublisher {

    /**
     * Starts the simulation with the given [configuration],
     * possibly giving a [roundTimeout] to limit the duration of each round.
     */
    fun start(configuration: World.Companion.Configuration, roundTimeout: Duration? = null)

    /**
     * Stops the current simulation.
     */
    fun stop()
}

/** The simulation controller. */
object SimulatorController : Controller, EventBusPublisher() {

    private var activeSimulation: Round? = null

    @Synchronized
    override fun start(configuration: World.Companion.Configuration, roundTimeout: Duration?) {
        check(activeSimulation == null) { "A simulation is already running. Please, stop it first." }
        Rnd.configure(RandomConfig.withTimeSeed())
        World.fromConfiguration(configuration).also { world ->
            val initialRound = Round.byCriteria(world) {
                it.elapsedTime >= (roundTimeout ?: Duration.INFINITE) || it.world.foods.toSet().isEmpty()
            }
            thread { simulationLoop(initialRound) }
            activeSimulation = initialRound
        }
    }

    private tailrec fun simulationLoop(round: Round, shouldStop: (Round) -> Boolean = { activeSimulation == null }) {
        logger.info { "Starting round ${round.number}..." }
        subscribers.forEach { round.world.register(it) }
        mas(round).start()
        logger.info { "Round ${round.number} ended." }
        if (!shouldStop(round) && !round.world.blobs.toSet().isEmpty()) {
            val nextRound = round.next()
            activeSimulation = nextRound
            simulationLoop(nextRound)
        }
    }

    private fun mas(round: Round) = mas {
        executionStrategy = ExecutionStrategy.discreteTimeExecution() // TODO!!!
        round.world.blobs.forEach { blobAgent(it) }
        environment(SimulationEnvironment(round))
    }

    @Synchronized
    override fun stop() = activeSimulation?.let {
        it.forceEnd()
        activeSimulation = null
    } ?: error("Cannot stop a non-existing simulation!")
}
