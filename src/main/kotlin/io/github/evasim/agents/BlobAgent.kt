package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.utils.Logic.selfSource
import it.unibo.jakta.agents.bdi.dsl.MasScope

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(blob: Blob) = agent(blob.id.value) {
    beliefs {
        fact { current_position(blob.position.x, blob.position.y) }
        fact { personality(atomOf(blob.personality.toString())) }
    }
    plans {
        +food(X, Y).fromPercept then {
            add(target(selfSource))
            execute(move_towards(X, Y))
        }
        +reached_food(F).fromPercept then {
            execute(update_velocity(0.0, 0.0))
            execute(collect_food(F))
        }
    }
}
