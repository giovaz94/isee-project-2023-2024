package io.github.evasim.model

import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

/** A blob in the simulation. */
interface Blob : Entity, EventPublisher {

    /** The blob initial place in the simulation. */
    val initialPlace: Placed<Shape>

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

    /** The blob personality. */
    val personality: Personality

    /** The blob's reproduction strategy. */
    val reproductionStrategy: ReproductionStrategy

    /** Applies a force to the blob, changing its velocity. */
    fun applyForce(force: Vector2D)

    /** Updates the blob velocity with the given vector. */
    fun updateVelocity(newVelocity: Vector2D)

    /** Updates the blob internal position based on the current velocity and elapsed time. */
    fun update(elapsedTime: Duration = 50.milliseconds)

    /** Returns whether the blob is alive. Dual to [isDead]. */
    fun isAlive(): Boolean

    /** Returns whether the blob is dead, i.e., has no health left. Dual to [isAlive]. */
    fun isDead(): Boolean

    /** Returns whether the blob can reproduce based on its current health. */
    fun canReproduce(): Boolean

    /** Clones this blob, creating a new instance with the specified parameters. */
    fun clone(
        id: Entity.Id = this.id,
        health: Health = this.health,
        personality: Personality = this.personality,
        position: Position2D = this.position,
        shape: Shape = this.shape,
        velocity: Vector2D = this.velocity,
        defaultDirection: Direction = this.defaultDirection,
        sightShape: Shape = this.sight.visibilityArea.shape,
        reproductionStrategy: ReproductionStrategy = this.reproductionStrategy,
    ): Blob

    /** The blob's factory methods. */
    companion object {
        /**
         * Creates a new [Blob] with the given id, shape, position, velocity,
         * default stationary direction, sight shape, and health.
         */
        operator fun invoke(
            id: Entity.Id,
            personality: Personality,
            position: Position2D,
            shape: Shape = Circle(radius = 20.0),
            velocity: Vector2D = Vector2D.ZERO,
            defaultDirection: Direction = Direction.DOWN,
            sightShape: Shape = Cone(radius = 80.0, fovDegrees = Degrees(value = 90.0)),
            health: Health = Health(min = 0.0, max = 2.0),
            reproductionRule: ReproductionStrategy = reproductionRule(),
        ): Blob = BlobImpl(
            id,
            shape,
            personality,
            position,
            velocity,
            defaultDirection,
            Sight(sightShape, position, velocity.normalized() ?: defaultDirection),
            health,
            reproductionRule,
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
        currentEnergy = max(current - health, min)
    }
}

private data class BlobImpl(
    override val id: Entity.Id,
    override val shape: Shape,
    override val personality: Personality,
    private var currentPosition: Position2D,
    private var currentVelocity: Vector2D,
    override val defaultDirection: Direction,
    override val sight: Sight,
    override val health: Health,
    override val reproductionStrategy: ReproductionStrategy,
) : Blob, EventBusPublisher() {

    override val initialPlace: Placed<Shape> = shape at currentPosition

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
        post(UpdatedBlob(copy()))
    }

    override fun isAlive(): Boolean = !isDead()

    override fun isDead(): Boolean = reproductionStrategy.invoke(health.current) == State.Dead

    override fun canReproduce(): Boolean = reproductionStrategy.invoke(health.current) == State.Reproducing

    override fun clone(
        id: Entity.Id,
        health: Health,
        personality: Personality,
        position: Position2D,
        shape: Shape,
        velocity: Vector2D,
        defaultDirection: Direction,
        sightShape: Shape,
        reproductionStrategy: ReproductionStrategy,
    ): Blob = copy(
        id,
        shape,
        personality,
        position, velocity, defaultDirection,
        Sight(sightShape, position, defaultDirection),
        health,
        reproductionStrategy,
    )
}
