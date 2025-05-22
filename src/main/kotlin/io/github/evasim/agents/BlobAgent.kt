package io.github.evasim.agents

import io.github.evasim.agents.Literals.find_food
import io.github.evasim.agents.Literals.food
import io.github.evasim.agents.Literals.move_towards
import io.github.evasim.agents.Literals.personality
import io.github.evasim.agents.Literals.reached_food
import io.github.evasim.agents.Literals.update_velocity
import io.github.evasim.model.Personality
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.tuprolog.core.Numeric

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(agentName: String, personality: Personality) = agent(agentName) {
    beliefs {
        fact { personality(personality.toString()) }
    }
    goals { achieve(find_food) }
    plans {
        +food(X, Y).fromPercept then {
            execute(move_towards(X, Y))
        }
        +reached_food(X, Y).fromPercept then {
            execute(update_velocity(Numeric.of(0.0), Numeric.of(0.0)))
        }
    }
}

/*
Goal
====
+!find_food
 |--> reazione dell'agente: in futuro può essere programmato un comportamento più intelligente
      del semplice continuare a muoversi verso la direzione prestabilita iniziale per cercare di "spottare" il cibo

+!reach_food => scatenato quando il food è nel sight dell'agente
 |--> reazione dell'agente deve "andare" verso il food

Belief
======
+ food => viene aggiunto alla belief base quando il food è nel sight dell'agente
+ reached_food => viene aggiunto quando l'agente ha raggiunto il food
*/
