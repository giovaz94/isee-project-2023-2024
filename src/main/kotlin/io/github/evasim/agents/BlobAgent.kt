package io.github.evasim.agents

import io.github.evasim.model.Personality
import it.unibo.jakta.agents.bdi.dsl.MasScope

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(name: String, personality: Personality) = agent(name) {
    // beliefs { fact{personality.toString()} }
    goals { achieve("findFood") }
    plans {
        +achieve("findFood") then {
            execute("print"("Hello, World. I'm the blob $name with $personality personality!"))
        }
        +"position"(X, Y).fromPercept then {
            execute("print"("Blob $name is at position (${X.asAtom()}, $Y)"))
        }
    }
}
