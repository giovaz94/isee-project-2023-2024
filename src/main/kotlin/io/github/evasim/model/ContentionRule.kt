package io.github.evasim.model

/**
 * A contention rule between two entities to determine how they compete for food, gaining energy,
 * based on their own characteristics and the characteristics of the other entity.
 * It returns the pair of energies gained by the two entities after the contention **in the same
 * order they have been provided in input**.
 */
fun interface ContentionRule : (Personality, Personality, Energy) -> Pair<Energy, Energy>

/** The ratio of energy gained by a hawk when attacking a dove. */
private const val HAWK_ATTACK_RATIO = 0.75

/** The ratio of energy gained by a dove when attacked by a hawk. */
private const val DOVE_ATTACK_RATIO = 0.25

/** A basic contention rule that determines how two entities (blobs) compete for food based on their personalities. */
val contentionRule: ContentionRule = ContentionRule { one, other, energy ->
    when (one) {
        is Hawk -> when (other) {
            is Hawk -> 0.0 to 0.0
            is Dove -> HAWK_ATTACK_RATIO * energy to DOVE_ATTACK_RATIO * energy
        }
        is Dove -> when (other) {
            is Hawk -> contentionRule(other, one, energy).swapped()
            is Dove -> 0.5 * energy to 0.5 * energy
        }
    }
}

internal fun <A, B> Pair<A, B>.swapped(): Pair<B, A> = Pair(second, first)
