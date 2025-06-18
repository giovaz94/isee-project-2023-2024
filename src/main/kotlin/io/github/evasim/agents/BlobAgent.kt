package io.github.evasim.agents

import io.github.evasim.model.Blob
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Var

private const val MIN_STEPS = 100
private const val MAX_STEPS = 200

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
        +achieve(forage) then {
            achieve(find_food)
            achieve(collect_food)
        }

        +achieve(find_food) onlyIf { status(exploring).fromSelf } then {
            achieve(change_direction)
            execute(random(N, MIN_STEPS, MAX_STEPS))
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

        +achieve(change_direction) then {
            execute(random(X, -1.0, 1.0))
            execute(random(Y, -1.0, 1.0))
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

        +achieve(collect_food) onlyIf { status(reached(F)).fromSelf } then {
            execute(collect(F))
        }

        +achieve("update_energy"(E)) onlyIf { energy(Y).fromSelf and (N `is` Y + E) } then {
            update(energy(N).fromSelf)
            execute(print("new energy ", N))
            achieve(forage)
        }

        // ENVIRONMENT PERCEPTIONS
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

        +bounce(D).fromPercept then {
            val invD = Var.of("invD")
            execute(inverse_direction(D, invD))
            update(direction(invD).fromSelf)
        }
        +ended_round.fromPercept then {
            execute("end_round")
        }

        // CONTENTION
        +contention("source"(S), P, E, F) then {
            execute(solve_contention(F, S, Atom.of(blob.personality.toString()), P, E, N))
            achieve("update_energy"(N))
        }

        +contention_result("source"(S), E) onlyIf { energy(Y).fromSelf and (N `is` Y + E) } then {
            update(energy(N).fromSelf)
            execute(print("new energy from solver ", N))
            achieve(forage)
        }
    }
    timeDistribution {
        Time.real(value = 50) // milliseconds
    }
}
