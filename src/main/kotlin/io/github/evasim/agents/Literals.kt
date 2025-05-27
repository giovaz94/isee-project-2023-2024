package io.github.evasim.agents

import io.github.evasim.utils.OwnName

/** The literal `print/2` internal action. */
internal val print by OwnName

/** The literal belief used to indicate the agent's current position. */
internal val my_position by OwnName

internal val current_position by OwnName

/** The literal belief used to indicate the agent's personality. */
internal val personality by OwnName

/** The literal goal used when the agent is hungry and needs to find food. */
internal val find_food by OwnName

/** The literal belief used to indicate a new food is in the agent's sight. */
internal val food by OwnName

/** The literal external action used to move the agent towards a target. */
internal val move_towards by OwnName

internal val target by OwnName

/** The literal external action used to update the agent's velocity. */
internal val update_velocity by OwnName

/** The literal external action used to move the agent towards a target. */
internal val update_direction by OwnName

/** The literal belief used to indicate that the agent has reached a piece of food. */
internal val reached_food by OwnName

internal val collect_food by OwnName
