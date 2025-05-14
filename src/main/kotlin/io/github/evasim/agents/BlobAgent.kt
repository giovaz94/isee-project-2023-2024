package io.github.evasim.agents

import io.github.evasim.model.Personality
import it.unibo.jakta.agents.bdi.dsl.MasScope

fun MasScope.blobAgent(name: String, personality: Personality) = agent(name) {
    // beliefs { fact{personality.toString()} }
    goals { achieve("sayHelloBlob") }
    plans {
        +achieve("sayHelloBlob") then {
            execute("print"("Hello, World. I'm the blob $name with $personality personality!"))
        }
    }
}


