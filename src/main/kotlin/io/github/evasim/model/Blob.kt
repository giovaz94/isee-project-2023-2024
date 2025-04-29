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

    /** The blob current direction as a 2D vector, which is actually a versor. */
    val direction: Vector2D
        get() = velocity.normalized()

    /** The blob current [Health]. */
    var health: Health

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
        /** Creates a new [Blob] with the given id, shape, position, velocity, sight shape, and health. */
        operator fun invoke(
            id: Entity.Id,
            shape: Shape,
            position: Position2D,
            velocity: Vector2D,
            sightShape: Shape = Cone(radius = 5.0, fovDegrees = 90.0),
            health: Health = Health(min = 0, max = 2),
        ): Blob = BlobImpl(
            id,
            shape,
            Sight(sightShape, position, velocity.normalized()),
            position,
            velocity,
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
    operator fun plus(health: Energy): Health

    /** Decreases the current health by the given amount. */
    operator fun minus(health: Energy): Health

    /** Health factory methods. */
    companion object {
        /** Creates a new [Health] with the given minimum and maximum values, initially set to the minimum. */
        operator fun invoke(min: Energy, max: Energy): Health = from(min, min, max)

        /** Creates a new [Health] with the given current, minimum, and maximum values. */
        fun from(current: Energy, min: Energy, max: Energy): Health = BasicHealth(current, min, max)
    }
}

private data class BasicHealth(
    override val current: Energy,
    override val min: Energy,
    override val max: Energy,
) : Health {
    override fun plus(health: Energy): Health = copy(current = min(current + health, max))
    override fun minus(health: Energy): Health = copy(current = max(current - health, 0))
}

private class BlobImpl(
    override val id: Entity.Id,
    override val shape: Shape,
    private var currentSight: Sight,
    private var currentPosition: Position2D,
    private var currentVelocity: Vector2D,
    private var currentHealth: Health,
) : Blob {

    override val initialPosition: Position2D = currentPosition

    override val position: Position2D
        get() = currentPosition

    override val velocity: Vector2D
        get() = currentVelocity

    override val sight: Sight
        get() = currentSight

    override var health: Health
        get() = currentHealth
        set(value) {
            currentHealth = value
        }

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
        currentSight.update(position, velocity.normalized())
    }

    override fun isDead(): Boolean = !isAlive()

    override fun isAlive(): Boolean = currentHealth.let { it.current > it.min }

    override fun isHungry(): Boolean = currentHealth.let { it.current < it.max }

    override fun canReproduce(): Boolean = currentHealth.let { it.current == it.max }
}
