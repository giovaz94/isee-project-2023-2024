package io.github.evasim.controller

import io.github.evasim.model.Rectangle
import io.github.evasim.model.World
import io.github.evasim.view.FXAppContext
import io.github.evasim.view.FXSimulatorView
import io.github.evasim.view.SimulatorView
import javafx.application.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlin.time.Duration

typealias Domain = World

/**
 * Represents a single user input in the system.
 *
 * The `UserInput` value class encapsulates a user-provided character input
 * for interaction within the application or system. It serves as a lightweight
 * wrapper for a `Char` value, ensuring a more structured and type-safe approach
 * when working with user inputs.
 *
 * This class can be used to register user inputs in controllers or other components
 * within the application to influence the system's behavior or state.
 *
 * @property input The character representing the user's input.
 */
@JvmInline value class UserInput(val input: Char)

/**
 * Represents an event in the system.
 *
 * The `Event` value class encapsulates a single event represented as a string.
 * It serves as a lightweight wrapper for event data to be processed within the application
 * or passed between components.
 *
 * This class is typically registered and managed within controllers to influence
 * the application's behavior or state.
 *
 * @property event The string representing the event.
 */
@JvmInline value class Event(val event: String)

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
     *
     * @param deltaTime The time duration since the last update, represented as a `Duration`.
     */
    fun updateDomain(deltaTime: Duration)

    /**
     * Registers a user input for processing within the controller.
     *
     * @param input The user input represented as a `UserInput` object.
     */
    fun registerUserInput(input: UserInput)

    /**
     * Registers an event within the controller for processing.
     *
     * @param event The event to be registered, represented as an `Event` object.
     */
    fun registerEvent(event: Event)

    /**
     * Renders the current state of the application or system.
     *
     * This function is responsible for rendering or displaying output based on
     * the current state of the controller, which may include visual, text-based,
     * or other forms of presentation.
     *
     * The state to be rendered is derived from the controller's current state,
     * which may be influenced by the registered user inputs and events.
     */
    fun render()
}

/**
 * Manages the lifecycle and operations related to the user interface for the simulator.
 *
 * This interface provides functionality to launch the user interface of the simulator
 * asynchronously, enabling interaction between the backend simulation logic and the
 * frontend graphical interface. It ensures proper initialization and binding of the view
 * and the controller for seamless functionality.
 */
interface UIControllerManager {
    /**
     * Launches the user interface asynchronously for the simulator view.
     *
     * This function initializes and starts the JavaFX application on a separate thread
     * using the FXSimulatorView class. It ensures that the UI is launched as part of
     * the application's asynchronous operations, allowing the simulation controller
     * to interact with the view once it is fully initialized.
     *
     * The function utilizes the `FXAppContext.viewReady` Deferred object to await
     * the readiness of the UI view, ensuring the controller and the view are linked
     * after the UI load completes.
     *
     * The simulator view's controller is then assigned to the current instance of
     * the `SimulatorController`, enabling bidirectional communication between
     * the control logic and the user interface.
     */
    suspend fun launchUIAsync()
}

/** The simulation controller. */
class SimulatorController(
    private var domain: Domain = World.empty(Rectangle(DEFAULT_WIDTH, DEFAULT_HEIGHT)),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : Controller, UIControllerManager {

    /**
     * The simulator view associated with the `SimulatorController`.
     *
     * This property represents the user interface component responsible for
     * rendering and interacting with the simulation domain. It serves as a
     * bridge between the controller, which manages the simulation logic,
     * and the UI, which displays and interacts with the simulation state.
     *
     * The view is initialized asynchronously when the user interface is launched
     * and is configured with the controller for bidirectional communication.
     *
     * This property must be set before rendering the simulation or interacting
     * with the view, ensuring proper linkage between the controller and the UI.
     */
    private lateinit var view: SimulatorView

    override suspend fun launchUIAsync() {
        CoroutineScope(dispatcher).launch {
            Application.launch(FXSimulatorView::class.java)
        }

        view = FXAppContext.viewReady.await()
        view.controller = this
    }

    override fun updateDomain(deltaTime: Duration) {
        domain = domain.update(deltaTime)
    }

    override fun registerUserInput(input: UserInput) {
        userInputs.add(input)
    }

    override fun registerEvent(event: Event) {
        events.add(event)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun render() {
        GlobalScope.launch(Dispatchers.JavaFx) {
            view.render(domain)
        }
    }

    /**
     * Contains companion objects and constants used in the `SimulatorController`.
     */
    companion object {

        /**
         * The default height value used within the simulator.
         *
         * This constant represents a predefined height used for initializing or rendering
         * elements in the simulation. It provides a consistent baseline for dimensions
         * where no specific height is otherwise specified.
         */
        private const val DEFAULT_HEIGHT = 50.0

        /**
         * Default width for initializing or rendering components within the simulator.
         *
         * This constant defines the baseline width value used in various parts of the
         * simulation or UI rendering. It provides a standard measurement to maintain
         * consistency and uniformity across different components.
         */
        private const val DEFAULT_WIDTH = 50.0
    }
}
