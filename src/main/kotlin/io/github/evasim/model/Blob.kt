package io.github.evasim.model

import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.DurationUnit

/** A blob in the simulation. */
interface Blob : Entity {

    /** The blob initial position in the simulation. */
    val initialPosition: Position2D

    /** The blob current velocity as a 2D vector with speed and direction. */
    val velocity: Vector2D

    /** The blob default direction when stationary. */
    val defaultDirection: Direction

    /** The blob current direction as a 2D versor. If the blob is stationary, it defaults to [defaultDirection]. */
    val direction: Direction
        get() = velocity.normalized() ?: defaultDirection

    /** The blob current [Health]. */
    val health: Health

    /** The blob current [Sight]. */
    val sight: Sight

    /** Applies a force to the blob, changing its velocity. */
    fun applyForce(force: Vector2D)

    /** Updates the blob velocity with the given vector. */
    fun updateVelocity(newVelocity: Vector2D)

    /** Updates the blob internal position based on the current velocity and elapsed time. */
    fun update(elapsedTime: Duration)

    /** Returns whether the blob is alive. Dual to [isDead]. */
    fun isAlive(): Boolean

    /** Returns whether the blob is dead, i.e., has no health left. Dual to [isAlive]. */
    fun isDead(): Boolean

    /** Returns whether the blob is hungry. */
    fun isHungry(): Boolean

    /** Returns whether the blob can reproduce based on its current health. */
    fun canReproduce(): Boolean

    /** The blob's factory methods. */
    companion object {
        /**
         * Creates a new [Blob] with the given id, shape, position, velocity,
         * default stationary direction, sight shape, and health.
         */
        operator fun invoke(
            id: Entity.Id,
            shape: Shape,
            position: Position2D,
            velocity: Vector2D,
            defaultDirection: Direction = Direction.DOWN,
            sightShape: Shape = Cone(radius = 5.0, fovDegrees = Degrees(value = 90.0)),
            health: Health = Health(min = 0, max = 2),
        ): Blob = BlobImpl(
            id,
            shape,
            position,
            velocity,
            defaultDirection,
            Sight(sightShape, position, velocity.normalized() ?: defaultDirection),
            health,
        )
    }
}

/** The health of an active entity in the simulation. */
interface Health {
    /** The current health amount. */
    val current: Energy

    /** The minimum health amount that the entity can have. */
    val min: Energy

    /** The maximum health amount that the entity can accumulate. */
    val max: Energy

    /** Increases the current health by the given amount. */
    operator fun plus(health: Energy)

    /** Decreases the current health by the given amount. */
    operator fun minus(health: Energy)

    /** Health factory methods. */
    companion object {
        /** Creates a new [Health] with the given minimum and maximum values, initially set to the minimum. */
        operator fun invoke(min: Energy, max: Energy): Health = from(min, min, max)

        /** Creates a new [Health] with the given current, minimum, and maximum values. */
        fun from(current: Energy, min: Energy, max: Energy): Health = BasicHealth(current, min, max)
    }
}

private data class BasicHealth(
    private var currentEnergy: Energy,
    override val min: Energy,
    override val max: Energy,
) : Health {
    override val current: Energy
        get() = currentEnergy
    override fun plus(health: Energy) {
        currentEnergy = min(currentEnergy + health, max)
    }
    override fun minus(health: Energy) {
        currentEnergy = max(current - health, 0)
    }
}

private class BlobImpl(
    override val id: Entity.Id,
    override val shape: Shape,
    private var currentPosition: Position2D,
    private var currentVelocity: Vector2D,
    override val defaultDirection: Direction,
    override val sight: Sight,
    override val health: Health,
) : Blob {

    override val initialPosition: Position2D = currentPosition

    override val position: Position2D
        get() = currentPosition

    override val velocity: Vector2D
        get() = currentVelocity

    override fun applyForce(force: Vector2D) = update(currentPosition, currentVelocity + force)

    override fun updateVelocity(newVelocity: Vector2D) = update(currentPosition, newVelocity)

    override fun update(elapsedTime: Duration) {
        val elapsedSeconds = elapsedTime.toDouble(DurationUnit.SECONDS)
        val newPosition = currentPosition + (currentVelocity * elapsedSeconds)
        update(newPosition, currentVelocity)
    }

    private fun update(position: Position2D, velocity: Vector2D) {
        currentPosition = position
        currentVelocity = velocity
        sight.update(position, velocity.normalized() ?: defaultDirection)
    }

    override fun isDead(): Boolean = !isAlive()

    override fun isAlive(): Boolean = health.let { it.current > it.min }

    override fun isHungry(): Boolean = health.let { it.current < it.max }

    override fun canReproduce(): Boolean = health.let { it.current == it.max }
}
