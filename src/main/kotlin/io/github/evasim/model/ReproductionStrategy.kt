package io.github.evasim.model

/**
 * A sealed interface representing the state of an entity in the simulation.
 */
sealed interface State {
    /** Alive entity state. */
    object Alive : State

    /** Dead entity state. */
    object Dead : State

    /** Reproducing entity state. */
    object Reproducing : State
}

/**
 * A strategy for determining the reproduction state of an entity based on its energy level.
 */
fun interface ReproductionStrategy : (Energy) -> State

/**
 * A Builder for creating a [ReproductionStrategy] based on the [aliveThreshold] and [reproductionThreshold].
 */
fun reproductionRule(aliveThreshold: Energy = 1.0, reproductionThreshold: Energy = 2.0): ReproductionStrategy =
    ReproductionStrategy { energy ->
        when {
            energy >= reproductionThreshold -> State.Reproducing
            energy >= aliveThreshold -> State.Alive
            else -> State.Dead
        }
    }
