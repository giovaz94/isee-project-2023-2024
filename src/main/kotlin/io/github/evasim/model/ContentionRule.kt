package io.github.evasim.model

/**
 * A contention rule between two entities to determine how they compete for food, gaining energy,
 * based on their own characteristics and the characteristics of the other entity.
 * It returns the pair of energies gained by the two entities after the contention **in the same
 * order they have been provided in input**.
 */
fun interface ContentionRule : (Personality, Personality, Energy) -> Pair<Energy, Energy>

/** A basic contention rule that determines how two entities (blobs) compete for food based on their personalities. */
@Suppress("detekt:all")
val contentionRule: ContentionRule = ContentionRule { one, other, energy ->
    when (one) {
        is Hawk -> when (other) {
            is Hawk -> Pair(0.0, 0.0)
            is Dove -> Pair(1.5 * energy, 0.5 * energy)
        }
        is Dove -> when (other) {
            is Hawk -> contentionRule(other, one, energy).swapped()
            is Dove -> {
                Pair(0.5 * energy, 0.5 * energy)
            }
        }
    }
}

internal fun <A, B> Pair<A, B>.swapped(): Pair<B, A> = Pair(second, first)
