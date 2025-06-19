package io.github.evasim.model

import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.TimeSource

/** A simulation round, i.e., an iteration. */
interface Round {

    /** The number of the round in the simulation. */
    val number: Int

    /** The elapsed time since the start of the round. */
    val elapsedTime: Duration

    /** The world in which the round is taking place. */
    val world: World

    /** @return true if the round has ended, false otherwise. */
    fun isEnded(): Boolean

    /**
     * Forces the round to end immediately, regardless of the end criteria.
     * This method can be used to stop the round prematurely.
     */
    fun forceEnd()

    /**
     * Advances to the next round, returning a new [Round] instance.
     * @throws IllegalStateException if the current round has not yet ended.
     */
    fun next(): Round

    /** Factory methods to create a [Round]s. */
    companion object {

        /**
         * Creates a new [Round] with the given [world] and an end criteria that checks if there are no foods left.
         * @param world The world in which the round is taking place.
         * @return A new [Round] instance.
         */
        fun byNoFood(world: World): Round = byCriteria(world) { it.world.foods.toSet().isEmpty() }

        /**
         * Creates a new [Round] with the given [world] and an end criteria defined by the provided [criteria] function.
         * @param world The world in which the round is taking place.
         * @param criteria A function that takes a [World] and returns true if the round should end.
         * @return A new [Round] instance.
         */
        fun byCriteria(world: World, criteria: (Round) -> Boolean): Round = RoundImpl(0, world, criteria)
    }
}

private data class RoundImpl(
    override val number: Int,
    override val world: World,
    private val endCriteria: (Round) -> Boolean,
) : Round {

    private val forcefullyStopped = AtomicReference(false)
    private val timer = AtomicReference(TimeSource.Monotonic.markNow())

    override val elapsedTime: Duration
        get() = timer.get().elapsedNow()

    override fun isEnded(): Boolean = forcefullyStopped.get() || endCriteria(this)

    override fun forceEnd() = forcefullyStopped.set(true)

    override fun next(): Round {
        check(isEnded()) { "Cannot advance to the next round when the current one ($number-th) is not yet ended." }
        return RoundImpl(number + 1, World.from(this), endCriteria)
    }
}
