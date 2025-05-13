package io.github.evasim.controller

import io.github.evasim.model.Circle
import io.github.evasim.model.HollowCircle
import io.github.evasim.model.Position2D
import io.github.evasim.model.SpawnZone
import io.github.evasim.model.World
import io.github.evasim.model.World.Companion.Configuration
import kotlin.time.Duration

typealias Domain = World

/**
 * Encapsulates a user-provided character input for interaction within the application or system.
 * It serves as a lightweight wrapper for a `Char` value, ensuring a more structured and type-safe approach
 * when working with user inputs.
 * TODO: Consider dropping this in favor of more specific Event types sent from boundaries to controller.
 * @property input The character representing the user's input.
 */
@JvmInline value class UserInput(val input: Char)

// TODO
// /**
// * Encapsulates a single event represented as a string.
// * It serves as a lightweight wrapper for event data to be processed within the application
// * or passed between components.
// * @property event The string representing the event.
// */
// @JvmInline value class Event(val event: String)

/**
 * Represents a controller interface responsible for managing user inputs, events,
 * domain updates, and rendering within a simulation or application.
 */
interface Controller {

    /**
     * A mutable list of user inputs in the form of `UserInput` objects.
     *
     * This property provides a collection to store and manage user-generated inputs
     * such as keystrokes or actions for processing within a simulation or application flow.
     * It is initialized as an empty mutable list upon first access.
     *
     * The list supports typical mutable operations like adding, removing, or modifying inputs.
     */
    val userInputs: MutableList<UserInput>
        get() = mutableListOf()

    /**
     * A mutable list of events represented by `Event` objects.
     *
     * This property acts as a collection to store and manage events,
     * facilitating event-driven behavior in the application such as
     * notifications, logging, or triggering specific actions.
     *
     * The list is initialized as an empty mutable list upon first access
     * and supports operations such as adding, removing, or modifying events
     * as part of the controller's functionality.
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
     * Renders the current state of the simulation or application.
     */
    fun render()
}

/** The simulation controller. */
object SimulatorController : Controller, EventSubscriber, EventBusPublisher() {

    private var domain: Domain = World.fromConfiguration(
        Configuration(
            shape = Circle(radius = 1_000.0),
            spawnZones = setOf(
                SpawnZone(HollowCircle(innerRadius = 800.0, outerRadius = 1_000.0), Position2D(1_000.0, 1_000.0)),
            ),
            blobsAmount = 120,
            hawkyBlobs = 60,
        ),
    )

    override fun updateDomain(deltaTime: Duration) {
        domain = domain.update(deltaTime)
    }

    override fun registerUserInput(input: UserInput) {
        userInputs.add(input)
    }

    override fun registerEvent(event: Event) {
        events.add(event)
    }

    override fun render() = post(UpdatedWorld(domain))
}
