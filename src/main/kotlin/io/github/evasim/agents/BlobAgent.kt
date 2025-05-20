package io.github.evasim.agents

import io.github.evasim.model.Personality
import it.unibo.jakta.agents.bdi.dsl.MasScope
import io.github.evasim.agents.Literals.my_position
import io.github.evasim.agents.Literals.print

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(name: String, personality: Personality) = agent(name) {
    beliefs {
        fact{ my_position(10, 20) }
    }
    goals { achieve("findFood") }
    plans {
        +achieve("findFood") then {
            execute(print("Hello, World. I'm the blob $name"))
        }
        +my_position(X, Y).fromPercept then {
            execute(print("Blob $name is at position", listOf(X, Y)))
        }
    }
}

//

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
