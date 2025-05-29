package io.github.evasim.agents

import io.github.evasim.model.Blob
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Var

private const val MIN_STEPS = 20
private const val MAX_STEPS = 50

/**
 * Blob agent factory.
 */
fun MasScope.blobAgent(blob: Blob) = agent(blob.id.value) {
    beliefs {
        fact { direction(0.0, 0.0) }
        fact { speed(10.0) }
        fact { status("exploring") }
    }
    actions {
        action(Random)
        action(WaypointDirection)
    }
    goals {
        achieve(round)
    }
    plans {
        // GOALS
        +achieve(round) then {
            achieve(find_food)
            achieve(collect_food)
            achieve(contention)
            achieve(go_home)
        }

        +achieve(find_food) onlyIf { status("exploring").fromSelf } then {
            achieve(change_direction)
            execute(random(N, MIN_STEPS, MAX_STEPS))
            achieve(move_on(N))
            achieve(find_food)
        }
        +achieve(find_food) onlyIf {
            status("targeting"(T, Q)).fromSelf and position(X, Y).fromPercept
        } then {
            val dirX = Var.of("DirX")
            val dirY = Var.of("DirY")
            execute(waypoint_direction(X, Y, T, Q, dirX, dirY))
            update(direction(dirX, dirY).fromSelf)
            achieve(move)
            achieve(find_food)
        }

        +achieve(change_direction) then {
            execute(random(X, -1.0, 1.0))
            execute(random(Y, -1.0, 1.0))
            update(direction(X, Y).fromSelf)
        }

        +achieve(move_on(0))
        +achieve(move_on(N)) onlyIf {
            N greaterThan 0 and (M `is` N - 1) and status("exploring").fromSelf
        } then {
            achieve(move)
            achieve(move_on(M))
        }
        +achieve(move_on(N)) onlyIf { N greaterThan 0 and (not(status("exploring").fromSelf)) }

        +achieve(move) onlyIf { direction(X, Y).fromSelf and speed(S).fromSelf } then {
            execute(update(X, Y, S))
        }

        // ENVIRONMENT PERCEPTIONS
        +food(X, Y).fromPercept then {
            update(status("targeting"(X, Y)).fromSelf)
        }
//        +reached_food(F).fromPercept then {
//            execute(update_velocity(0.0, 0.0))
//            execute(collect_food(F))
//        }
    }
    timeDistribution {
        Time.real(value = 50) // milliseconds
    }
}

/*
direction(0, 0).
speed(0).
status(exploring).

!round.

+!round <-
  !find_food;
  !collect_food;
  !contention;
  !back_home.

+!find_food : status(exploring) <-
  !change_direction;
  random(N, 1, 20); // N is the number of steps to take following the direction
  !move_on(N);
  !find_food.

+!find_food : status(targeting(F)) & position(PosX, PosY) <-
  waypoint_direction(PosX, PosY, F, DirX, DirY);
  -+direction(DirX, DirY);
  !move.
  !find_food.

+!find_food : status(reached(F)) <- true.

+!move_on(0) <- true.

+!move_on(N) : N > 0 & status(exploring) <-
  !move;
  !move_on(N - 1).

+!move_on(N) : N > 0 & (obstacle(X, Y) | not(status(exploring))) <- true.

// TODO: obstacle avoidance. How to deal with multiple obstacles?
+!change_direction : not(obstacle)) <-
  random(X, -1, 1);
  random(Y, -1, 1);
  -+direction(X, Y).

+!change_direction : obstacle() & direction(X, Y) <-
  // TODO...
  -+direction(-X, -Y); // Reverse direction when an obstacle is detected

+!move : direction(X, Y) & speed(V) <-
  !update_position(X, Y, V); // External action

+!collect_food : food(F) <-
  collect_food(F, IsCollected); // External action
  if (IsCollected) {
    -+status(contending(F));
    !contention;
  } else {
    -+status(exploring);
    !find_food;
  }

+target_food(F) <- // Belief added from the environment
  status(targeting(F)).

+reached_food(F) : status(targeting(F)) <- // Belief added from the environment
  status(reached(F)).

!contention : status(contending(F)) <-
  ...
 */
