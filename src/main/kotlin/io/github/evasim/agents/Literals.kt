package io.github.evasim.agents

import io.github.evasim.utils.OwnName

/** Literals used in agents knowledge base. */
internal object Literals {
    /** The literal jason `print/2` internal action. */
    val print: String by OwnName

    /** The literal belief used to indicate the agent's current position. */
    val my_position: String by OwnName

    /** The literal belief used to indicate the agent's personality. */
    val personality: String by OwnName

    /** The literal goal used when the agent is hungry and needs to find food. */
    val find_food: String by OwnName

    /** The literal belief used to indicate a new food is in the agent's sight. */
    val food: String by OwnName

    /** The literal external action used to move the agent towards a target. */
    val move_towards: String by OwnName

    /** The literal external action used to update the agent's velocity. */
    val update_velocity: String by OwnName

    /** The literal external action used to move the agent towards a target. */
    val update_direction: String by OwnName

    /** The literal belief used to indicate that the agent has reached food. */
    val reached_food: String by OwnName
}
