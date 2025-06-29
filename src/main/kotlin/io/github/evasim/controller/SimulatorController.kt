package io.github.evasim.controller

import io.github.evasim.agents.SimulationEnvironment
import io.github.evasim.agents.blobAgent
import io.github.evasim.model.EventBusPublisher
import io.github.evasim.model.EventPublisher
import io.github.evasim.model.Round
import io.github.evasim.model.SimulationEnded
import io.github.evasim.model.SimulationStarted
import io.github.evasim.model.World
import io.github.evasim.utils.FileLogger
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
     * Starts the simulation with the given [worldConfiguration],
     * possibly giving a [roundTimeout] to limit the duration of each round and an optional [runSeed]
     * to which is possible to control the reproducibility of the simulation.
     */
    fun start(
        worldConfiguration: World.Companion.Configuration,
        roundTimeout: Duration = Duration.INFINITE,
        runSeed: Long = System.currentTimeMillis(),
    )

    /**
     * Stops the current simulation.
     */
    fun stop()
}

/** The simulation controller. */
object SimulatorController : Controller, EventBusPublisher() {

    private var activeSimulation: Round? = null

    @Synchronized
    override fun start(worldConfiguration: World.Companion.Configuration, roundTimeout: Duration, runSeed: Long) {
        check(activeSimulation == null) { "A simulation is already running. Please, stop it first." }
        Rnd.configure(RandomConfig.bySeed(runSeed))
        World.fromConfiguration(worldConfiguration).also { world ->
            val initialRound = Round.byCriteria(world) {
                it.elapsedTime >= roundTimeout || it.world.foods.toSet().isEmpty()
            }
            thread {
                val fileLogger = FileLogger.defaultRoundLogger("rounds.log")
                post(SimulationStarted)
                simulationLoop(initialRound, fileLogger)
                post(SimulationEnded)
                fileLogger.close()
            }
            activeSimulation = initialRound
        }
    }

    private tailrec fun simulationLoop(
        round: Round,
        fileLogger: FileLogger<Round>,
        shouldStop: (Round) -> Boolean = { activeSimulation == null },
    ) {
        logger.info { "Starting round ${round.number}..." }
        fileLogger.write(round)
        subscribers.forEach { round.world.register(it) }
        mas(round).start()
        logger.info { "Round ${round.number} ended." }
        if (!shouldStop(round) && !round.world.blobs.toSet().isEmpty()) {
            val nextRound = round.next()
            activeSimulation = nextRound
            simulationLoop(nextRound, fileLogger)
        }
    }

    private fun mas(round: Round) = mas {
        executionStrategy = ExecutionStrategy.discreteTimeExecution()
        round.world.blobs.forEach { blobAgent(it) }
        environment(SimulationEnvironment(round))
    }

    @Synchronized
    override fun stop() = activeSimulation?.let {
        it.forceEnd()
        activeSimulation = null
    } ?: error("Cannot stop a non-existing simulation!")
}
