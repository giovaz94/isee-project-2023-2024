package io.github.evasim.agents

import io.github.evasim.agents.Literals.find_food
import io.github.evasim.agents.Literals.my_position
import io.github.evasim.agents.Literals.personality
import io.github.evasim.agents.Literals.print
import io.github.evasim.model.Personality
import it.unibo.jakta.agents.bdi.dsl.MasScope

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(name: String, personality: Personality) = agent(name) {
    beliefs {
        fact { personality(personality.toString()) }
    }
    goals { achieve(find_food) }
    plans {
        +achieve(find_food) then {
            execute(print("Hello, World. I'm the blob $name"))
        }
        +my_position(X, Y).fromPercept then {
            execute(print("Blob at position", Pair(X, Y)))
        }
    }
}

/*
Beliefs:
========

my_position(X, Y)
personality(Hawk / Dove)
food(X, Y)

Initial Goals:
==============

!findFood

Plans:
=====

+!findFood <-
    peek a random velocity

+food(X, Y) <-
    stop myself
*/
