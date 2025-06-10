package io.github.evasim.model

typealias Simulation = Sequence<Round>

/** A simulation round, i.e., an iteration. */
interface Round {

    /** The number of the round in the simulation. */
    val number: Int

    /** The world in which the round is taking place. */
    val world: World

    /** @return true if the round has ended, false otherwise. */
    fun isEnded(): Boolean

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
        fun byNoFood(world: World): Round = byCriteria(world) { it.foods.toSet().isEmpty() }

        /**
         * Creates a new [Round] with the given [world] and an end criteria defined by the provided [criteria] function.
         * @param world The world in which the round is taking place.
         * @param criteria A function that takes a [World] and returns true if the round should end.
         * @return A new [Round] instance.
         */
        fun byCriteria(world: World, criteria: (World) -> Boolean): Round = RoundImpl(0, world, criteria)
    }
}

private data class RoundImpl(
    override val number: Int,
    override val world: World,
    private val endCriteria: (World) -> Boolean,
) : Round {

    override fun isEnded(): Boolean = endCriteria(world)

    override fun next(): Round {
        check(isEnded()) { "Cannot advance to the next round when the current one ($number-th) is not yet ended." }
        return RoundImpl(number + 1, World.from(world), endCriteria)
    }
}
