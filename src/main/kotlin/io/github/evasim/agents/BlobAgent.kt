package io.github.evasim.agents

import io.github.evasim.model.Blob
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.plans.PlansScope
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Var

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(blob: Blob) = agent(blob.id.value) {
    beliefs {
        fact { energy(0.0) }
        fact { direction(tupleOf(0.0, 0.0)) }
        fact { speed(term = 20.0) }
        fact { status(exploring) }
    }
    actions {
        action(Random)
        action(WaypointDirection)
        action(InverseDirection)
        action(EndRound)
    }
    goals { achieve(forage) }
    plans {
        forage()
        findFood()
        movement()
        collectFood(blob)
        contention(blob)
        endedRound()
    }
    timeDistribution {
        Time.real(value = 50) // TODO
    }
}

private fun PlansScope.forage() {
    +achieve(forage) then {
        update(status(exploring).fromSelf)
        achieve(find_food)
        achieve(collect_food)
    }
}

private fun PlansScope.findFood(minSteps: Int = 100, maxSteps: Int = 200) {
    +achieve(find_food) onlyIf { status(exploring).fromSelf } then {
        achieve(change_direction)
        execute(random(N, minSteps, maxSteps))
        achieve(move_on(N))
        achieve(find_food)
    }
    +achieve(find_food) onlyIf { status(targeting(T)).fromSelf and position(P).fromPercept } then {
        execute(waypoint_direction(P, T, D))
        update(direction(D).fromSelf)
        achieve(move)
        achieve(find_food)
    }
    +achieve(find_food) onlyIf { status(reached(`_`)).fromSelf }
}

private fun PlansScope.movement(minValue: Double = -1.0, maxValue: Double = 1.0) {
    +achieve(change_direction) then {
        execute(random(X, minValue, maxValue))
        execute(random(Y, minValue, maxValue))
        update(direction(tupleOf(X, Y)).fromSelf)
    }
    +achieve(move_on(0))
    +achieve(move_on(N)) onlyIf { N greaterThan 0 and status(exploring).fromSelf and (M `is` N - 1) } then {
        achieve(move)
        achieve(move_on(M))
    }
    +achieve(move_on(N)) onlyIf { N greaterThan 0 and not(status(exploring).fromSelf) }
    +achieve(move) onlyIf { direction(D).fromSelf and speed(S).fromSelf } then {
        execute(update(D, S))
    }
    +bounce(D).fromPercept then {
        val invD = Var.of("invD")
        execute(inverse_direction(D, invD))
        update(direction(invD).fromSelf)
    }
}

private fun PlansScope.collectFood(blob: Blob) {
    +achieve(collect_food) onlyIf { status(reached(F)).fromSelf } then {
        execute(collect(F))
    }
    +food(P).fromPercept onlyIf { not(status(reached(`_`)).fromSelf) } then {
        update(status(targeting(P)).fromSelf)
    }
    -food(P).fromPercept onlyIf { not(status(reached(`_`)).fromSelf) } then {
        update(status(exploring).fromSelf)
    }
    +reached_food(F).fromPercept then {
        update(status(reached(F)).fromSelf)
    }
    +collected_food(F, List.empty(), `_`).fromPercept onlyIf { status(reached(F)).fromSelf } then {
        update(status(exploring).fromSelf)
        achieve(forage)
    }
    +collected_food(F, B, E).fromPercept onlyIf { status(reached(F)).fromSelf } then {
        execute(check_contention(B, Atom.of(blob.personality.toString()), E, F))
    }
}

private fun PlansScope.contention(blob: Blob) {
    +contention(source(S), P, E, F) then {
        execute(solve_contention(F, S, Atom.of(blob.personality.toString()), P, E, N))
        achieve(update_energy(N))
    }
    +contention_result(source(S), E) onlyIf { energy(Y).fromSelf and (N `is` Y + E) } then {
        update(energy(N).fromSelf)
        achieve(forage)
    }
    +achieve(update_energy(E)) onlyIf { energy(Y).fromSelf and (N `is` Y + E) } then {
        update(energy(N).fromSelf)
        achieve(forage)
    }
}

private fun PlansScope.endedRound() {
    +ended_round.fromPercept then {
        execute(stop_agent)
    }
}
