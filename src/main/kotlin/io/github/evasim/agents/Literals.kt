package io.github.evasim.agents

import io.github.evasim.utils.OwnName

/**********************************************************************************************************************
 *                                                   === BELIEFS ===
 *********************************************************************************************************************/

/**
 * **Belief: `personality(P)`**.
 *
 * Represents the agent's personality type, either `hawk` or `dove`.
 */
internal val personality by OwnName

/**
 * **Belief: `direction((X, Y))`**.
 *
 * Represents the agent's current direction in a 2D space, expressed as a tuple of Real numbers (X, Y) like a vector.
 * @see io.github.evasim.utils.Logic.castToVector2D
 */
internal val direction by OwnName

/**
 * **Belief: `speed(S)`**.
 *
 * Represents the agent's current speed, expressed as a Real number.
 */
internal val speed by OwnName

/**
 * **Belief: `energy(E)`**.
 *
 * Represents the agent's current energy level represented as a Real number.
 */
internal val energy by OwnName

/**
 * **Belief: `status(S)`**.
 *
 * Represents the agent's current state; possible values are [exploring], [targeting] or [reached].
 * This belief is used to track the agent's behavioral state during foraging.
 */
internal val status by OwnName

/**
 * **Status value: `exploring`**.
 *
 * A status value indicating the agent is in exploration mode, randomly searching for food.
 */
internal val exploring by OwnName

/**
 * **Status value: `targeting((X, Y))`**.
 *
 * A status value indicating the agent is moving toward a specific target at position `(X, Y)`.
 * @see io.github.evasim.utils.Logic.castToVector2D
 */
internal val targeting by OwnName

/**
 * **Status value: `reached(FoodId)`**.
 *
 * A status value indicating the agent has reached the food identified by `FoodId`.
 */
internal val reached by OwnName

/**
 * **Percept source belief: `food(-Position)`**.
 *
 * Perception added by the environment indicating that food is in the agent's sight.
 *
 * - `Position` is a tuple representing the coordinates of the food in the 2D space, like `(X, Y)`.
 */
internal val food by OwnName

/**
 * **Percept source belief: `reached_food(-FoodId)`**.
 *
 * Perception added by the environment indicating that the agent has reached food identified by `FoodId`.
 */
internal val reached_food by OwnName

/**
 * **Percept: `bounce(-NewDirection)`**.
 *
 * Perception added by the environment indicating the agent is colliding with a boundary and has to bounce back.
 *
 * - `NewDirection` is a tuple representing the new direction vector `(X, Y)` after bouncing.
 */
internal val bounce by OwnName

/**
 * **Percept source belief: `collected_food(-FoodId, -ContendingBlobs, -Energy)`**.
 *
 * Perception added by the environment to the agent's belief base as a result of the [collect] external action to
 * indicate that food identified by `FoodId` with `Energy` has been collected by agents `ContendingBlobs`.
 *
 * - `FoodId` is the ID of the food that has been collected.
 * - `ContendingBlobs` is a [it.unibo.tuprolog.core.List] of Blob IDs contending for the food.
 * - `Energy` is the amount of energy gained from collecting the food expressed as a Real number.
 */
internal val collected_food by OwnName

/**
 * **Percept source belief: `not_collected_food/0`**.
 *
 * Perception added by the environment to the agent's belief base as a result of the [collect] external action to
 * indicate that attempted food collection failed, because the food was already collected by another agent (race
 * contention).
 */
internal val not_collected_food by OwnName

/**
 * **Percept source belief: `contention(source(BlobId), Personality, Energy, Food)`**.
 *
 * Perception added by the environment to the agent's belief base, used to signal the agent to take care of the
 * contention outcome
 */
internal val contention by OwnName

/**
 * **Percept source belief: `contention_result(source(BlobId), `Energy`)`**.
 *
 * Perception added by the environment to the agent's belief base as a result of the [check_contention] external action
 * providing the result of a contention resolution with agent `BlobId`, providing energy `Energy`.
 */
internal val contention_result by OwnName

/**
 * **Percept source belief: `position(-Position)`**.
 *
 * Perception added by the environment to the agent's belief base indicating the agent's current position in the world.
 *
 * - `Position` is a tuple representing the agent's coordinates in the 2D space, like `(X, Y)`.
 *
 * @see io.github.evasim.utils.Logic.castToVector2D
 */
internal val position by OwnName

/**
 * **Percept source belief: `ended_round/0`**.
 *
 * Perception added by the environment to the agent's belief base to signal that the current round has ended.
 */
internal val ended_round by OwnName

/**
 * **Belief component: `source(S)`**.
 *
 * A belief component that identifies the source agent `S` in communications.
 */
internal val source by OwnName

/**********************************************************************************************************************
 *                                                     === GOALS ===
 *********************************************************************************************************************/

/**
 * **Goal: `forage/0`**.
 *
 * This is an achievement goal that causes the agent to begin foraging for food.
 * The agent will search the environment for foods and attempt to collect them.
 */
internal val forage by OwnName

/**
 * **Goal: `find_food/0`**.
 *
 * This is an achievement goal that causes the agent to actively search for food in the environment by exploring it.
 */
internal val find_food by OwnName

/**
 * **Goal: `move_on(NSteps)`**.
 *
 * This is an achievement goal that causes the agent to move NSteps steps in the current direction.
 * Used for planned movement sequences during exploration.
 */
internal val move_on by OwnName

/**
 * **Goal: `move/0`**.
 *
 * This is an achievement goal that causes the agent to move one step in the current [direction].
 */
internal val move by OwnName

/**
 * **Goal: `change_direction/0`**.
 *
 * This is an achievement goal that causes the agent to **randomly** change its [direction].
 */
internal val change_direction by OwnName

/**
 * **Goal: `waypoint_direction(+Position, +Target, -NewDirection)`**.
 *
 * Calculates the new direction to move from a given position towards a target position.
 *
 * - `Position` is the agent's current position in the environment.
 * - `Target` is the target position the agent wants to reach.
 * - `NewDirection` is the resulting direction vector that the agent should take to reach the target.
 */
internal val waypoint_direction by OwnName

/**
 * **Goal: `collect_food/0`**.
 *
 * This is an achievement goal that causes the agent to collect food when it has reached it.
 */
internal val collect_food by OwnName

/**********************************************************************************************************************
 *                                              === EXTERNAL ACTIONS ===
 *********************************************************************************************************************/

/**
 * **External action: `collect(+FoodId)`**.
 *
 * Attempts to collect food identified by `FoodId` from the environment.
 */
internal val collect by OwnName

/**
 * **External action: `update(+Direction, +Speed)`**.
 *
 * Moves the agent in the environment based on the specified direction and speed.
 *
 * - `Direction` is a tuple representing the direction vector `(X, Y)` in the 2D space.
 * - `Speed` is a Real number representing the speed at which the agent should move.
 *
 * @see io.github.evasim.utils.Logic.castToVector2D
 */
internal val update by OwnName

/**
 * **External action: `check_contention(+BlobList, +Personality, +Energy, +FoodId)`**.
 *
 * Check if a Food, represented by FoodId is contended by two agents
 *
 * `BlobList` is a list representing the IDs of the agents that are contending the food.
 * `Personality` is an atom representing the personality of the Blob that's currently checking
 *  the contention.
 *  `Energy` is a Real representing the total energy of the food.
 *  `FoodId` is an atmo representing the ID of the contended food .
 */
internal val check_contention by OwnName

/**
 * **External action:
 * `solve_contention(+FoodId, +ContenderId, +SolverPersonality, +ContenderPersonality, +TotalEnergy, -SolverEnergy)`**.
 *
 *  external action that resolves contention for food identified by `FoodId` between the agent executing it and the
 *  contender identified by `ContenderId`, using the personalities of both agents and the total energy available.
 *
 * `FoodId` is an atom representing the ID of the contended food .
 * `ContenderID` an atom representing the ID of the contended agent
 * `SolverPersonality` an atom representing the personality of the solver
 * `ContenderPersonality` an atom representing the personality of the contender
 * `TotalEnergy` is a real representing the total energy of the food
 * `SolverEnergy` is an output variable representing the resulting solver energy.
 *
 */
internal val solve_contention by OwnName

/**
 * **External action: `remove_food(FoodId)`**.
 *
 * Removes food identified by `FoodId` from the environment after it has been collected.
 */
internal val remove_food by OwnName

/**
 * **External action: `update_energy(Energy)`**.
 *
 * Updates the agent's energy level by a certain amount, represented as a Real number `Energy`.
 */
internal val update_energy by OwnName

/**********************************************************************************************************************
 *                                              === INTERNAL ACTIONS ===
 *********************************************************************************************************************/

/**
 * **Internal action: `random(-N, +Min, +Max)`**.
 *
 * Generates a random integer between `Min` and `Max`, inclusive, and binds it to the variable `N`.
 * `Min` and `Max` can be either integers or doubles, depending on the context.
 * @see [Random]
 */
internal val random by OwnName

/**
 * **Internal action: `inverse_direction(+CurrentDirection, -InverseDirection)`**.
 *
 * Calculates the inverse direction `InverseDirection` based on the current direction `CurrentDirection`.
 *
 * - `CurrentDirection` is a tuple representing the current direction vector `(X, Y)`.
 * - `InverseDirection` is a tuple representing the new direction vector `(X, Y)` after applying a refracted angle
 */
internal val inverse_direction by OwnName

/**
 * **Internal action: `stop_agent/0`**.
 *
 * Stops, i.e., terminates, the agent's execution.
 */
internal val stop_agent by OwnName
