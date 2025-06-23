package io.github.evasim.utils

import kotlin.random.Random

/**
 * Provides a singleton random number generator that can be configured with a [RandomConfig] to control
 * the determinism of the application run, without the need to pass everywhere the seed or the random instance.
 * An incorrect configuration before its usage will throw an error.
 */
object Rnd {
    private var configuration: RandomConfig? = null

    private val rnd: Random
        get() = configuration?.rnd ?: error("`Rnd` not appropriately configured!")

    /** Configures the random number generator with a [RandomConfig]. */
    fun configure(config: RandomConfig) {
        configuration = config
    }

    /** @return `true` if the random number generator is configured, `false` otherwise. */
    fun isConfigured(): Boolean = configuration != null

    /** Resets the random number generator configuration. */
    fun reset() {
        configuration = null
    }

    /** @return a random [Int] in the range [from, until). */
    fun nextInt(from: Int, until: Int): Int = rnd.nextInt(from, until)

    /** @return a random [Double] in the range [from, until). */
    fun nextDouble(from: Double = 0.0, until: Double = 1.0): Double = rnd.nextDouble(from, until)
}

/** Represents the configuration for the underlying random number generator [rnd]. */
class RandomConfig private constructor(val rnd: Random) {

    /** Factory methods to create a [RandomConfig] with different seeds. */
    companion object {

        /** Creates a [RandomConfig] using a specific seed. */
        fun withSeed(seed: Long) = RandomConfig(Random(seed))

        /** Creates a [RandomConfig] using the current time as seed. */
        fun withTimeSeed() = RandomConfig(Random(System.currentTimeMillis()))

        /** Creates a [RandomConfig] using a string as seed, which is converted to a hash code. */
        fun withStringSeed(str: String) = RandomConfig(Random(str.hashCode().toLong()))

        /** Creates a default [RandomConfig] with no specific seed, fallback to [Random.Default]. */
        fun default() = RandomConfig(Random.Default)
    }
}
