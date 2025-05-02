package io.github.evasim.model

/**
 * A contention rule between two entities to determine how they compete for food, gaining energy,
 * based on their own characteristics and the characteristics of the other entity.
 */
fun interface ContentionRule : (Blob, Blob, Food) -> Pair<Energy, Energy>

/** A basic contention rule that determines how two entities (blobs) compete for food based on their personalities. */
val basicContentionRule: ContentionRule = ContentionRule { one, other, food ->
    when (one.personality) {
        is Hawk -> when (other.personality) {
            is Hawk -> Pair(0.0, 0.0)
            is Dove -> Pair(3 / 2 * food.totalEnergy, 1 / 2 * food.totalEnergy)
        }
        is Dove -> when (other.personality) {
            is Hawk -> basicContentionRule(other, one, food).swapped()
            is Dove -> Pair(1 / 2 * food.totalEnergy, 1 / 2 * food.totalEnergy)
        }
    }
}

internal fun <A, B> Pair<A, B>.swapped(): Pair<B, A> = Pair(second, first)
