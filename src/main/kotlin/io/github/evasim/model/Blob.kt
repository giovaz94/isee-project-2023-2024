package io.github.evasim.model

import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

/** A blob in the simulation. */
interface Blob : Entity {

    /** The blob initial position in the simulation. */
    val initialPosition: Position2D

    /** The blob current velocity as a 2D vector with speed and direction. */
    val velocity: Vector2D

    /** The blob current [Health]. */
    var health: Health

    /** The blob current [Sight]. */
    val sight: Sight

    /** Applies a force to the blob, changing its velocity. */
    fun applyForce(force: Vector2D)

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

/** Represents the visual perception capability of an entity within the simulation. */
interface Sight {

    /**
     * The portion of the environment currently visible to the entity.
     * This area determines what the entity can perceive and react to.
     */
    val visibilityArea: Shape

    /** Sight factory methods. */
    companion object {
        /** Creates a new [Sight] instance from the given shape. */
        fun from(shape: Shape): Sight = object : Sight {
            override val visibilityArea: Shape = shape
        }
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

/*
private class BlobImpl(
    override val initialPosition: Position2D,
    private var currentVelocity: Vector2D,
    private var currentHealth: Health,
    override val sight: Sight,
    override val id: Entity.Id,
    override val position: Position2D,
    override val shape: Shape
) : Blob {
    override val velocity: Vector2D
        get() = currentVelocity

    override var health: Health
        get() = currentHealth
        set(value) { currentHealth = value }

    override fun applyForce(force: Vector2D) {
        currentVelocity += force
    }

    override fun update(elapsedTime: Duration) {
        TODO("Not yet implemented")
    }

    override fun isDead(): Boolean = !isAlive()

    override fun isAlive(): Boolean = currentHealth.let { it.current > it.min }

    override fun isHungry(): Boolean = currentHealth.let { it.current < it.max }

    override fun canReproduce(): Boolean = currentHealth.let { it.current == it.max }
}
*/
