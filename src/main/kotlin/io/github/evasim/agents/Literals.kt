package io.github.evasim.agents

import io.github.evasim.utils.OwnName

/** The literal `print/2` internal action. */
internal val print by OwnName

internal val random by OwnName

internal val direction by OwnName

internal val speed by OwnName

internal val move by OwnName

internal val forage by OwnName

internal val status by OwnName

internal val change_direction by OwnName

internal val move_on by OwnName

/** The literal goal used when the agent is hungry and needs to find food. */
internal val find_food by OwnName

internal val collect_food by OwnName

internal val collect by OwnName

internal val collected_food by OwnName

internal val contention by OwnName

internal val go_home by OwnName

internal val update by OwnName

internal val position by OwnName

/** The literal belief used to indicate a new food is in the agent's sight. */
internal val food by OwnName

/** The literal belief used to indicate that the agent has reached a piece of food. */
internal val reached_food by OwnName

internal val waypoint_direction by OwnName

internal val exploring by OwnName

internal val targeting by OwnName

internal val reached by OwnName

internal val bounce by OwnName

internal val inverse_direction by OwnName
