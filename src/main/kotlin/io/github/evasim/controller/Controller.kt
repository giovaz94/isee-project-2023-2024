package io.github.evasim.controller

import io.github.evasim.agents.blobAgent
import io.github.evasim.agents.environment.SimulationEnvironment
import io.github.evasim.model.World
import io.github.evasim.model.World.Companion.Configuration
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import kotlin.concurrent.thread
import kotlin.time.Duration

typealias Domain = World

/**
 * Encapsulates a user-provided character input for interaction within the application or system.
 * It serves as a lightweight wrapper for a `Char` value, ensuring a more structured and type-safe approach
 * when working with user inputs.
 * @property input The character representing the user's input.
 */
@JvmInline value class UserInput(val input: Char)

/**
 * Represents the simulation controller interface responsible for managing user inputs, events,
 * domain updates, and rendering.
 */
interface Controller {

    /**
     * A mutable list of user inputs in the form of `UserInput` objects.
     *
     * This property provides a collection to store and manage user-generated inputs
     * such as keystrokes or actions for processing within a simulation or application flow.
     * It is initialized as an empty mutable list upon first access.
     */
    val userInputs: MutableList<UserInput>
        get() = mutableListOf()

    /**
     * A mutable list of events.
     * This property acts as a collection to store and manage events, enabling event-driven behavior.
     */
    val events: MutableList<Event>
        get() = mutableListOf()

    /**
     * Updates the domain state of the controller based on the given time delta.
     * @param deltaTime The time duration since the last update, represented as a `Duration`.
     */
    fun updateDomain(deltaTime: Duration)

    /**
     * Registers a user input for processing within the controller.
     * @param input The user input represented as a `UserInput` object.
     */
    fun registerUserInput(input: UserInput)

    /**
     * Registers an event within the controller for processing.
     * @param event The event to be registered, represented as an `Event` object.
     */
    fun registerEvent(event: Event)

    /**
     * Starts the simulation with the given [configuration].
     */
    fun start(configuration: Configuration)

    /**
     * Stops the current simulation.
     */
    fun stop()

    /**
     * Renders the current state of the simulation or application.
     */
    fun render()
}

/** The simulation controller. */
object SimulatorController : Controller, EventSubscriber, EventBusPublisher() {

    private val engine = SimulationEngine(this)
    private var domain: Domain? = null

    @Synchronized
    override fun updateDomain(deltaTime: Duration) {
        domain = domain?.update(deltaTime) ?: error("It is not possible to update a non-existing domain!")
    }

    @Synchronized
    override fun registerUserInput(input: UserInput) {
        userInputs.add(input)
    }

    @Synchronized
    override fun registerEvent(event: Event) {
        events.add(event)
    }

    @Synchronized
    override fun start(configuration: Configuration) {
        require(domain == null) { "A simulation is already running. Please, stop it first." }
        thread { engine.start() }
        World.fromConfiguration(configuration)
            .also { domain = it }
            .let { startMas(it) }
    }

    // TODO: think if this is the right place where to start the agents, e.g., in the rounds manager.
    private fun startMas(domain: Domain) = mas {
        environment(SimulationEnvironment(domain))
        domain.blobs.forEach {
            blobAgent(it.id.value, it.personality)
        }
        executionStrategy = ExecutionStrategy.oneThreadPerMas()
    }.start()

    @Synchronized
    override fun stop() {
        requireNotNull(domain) { "Cannot stop a non-existing simulation!" }
        domain = null
        engine.stop()
    }

    @Synchronized
    override fun render() {
        post(UpdatedWorld(domain ?: error("Cannot render a non-existing simulation!")))
    }
}
