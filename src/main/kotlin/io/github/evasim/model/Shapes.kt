package io.github.evasim.model

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * A shape in a two-dimensional space relative whose coordinates are **local**, i.e., relative to the origin.
 * Its "central" position is always at the origin (0, 0).
 * A `Shape` does not know where it is placed in the world. To place it use a [Placed] instead.
 * @see [Placed]
 * @see [at]
 */
sealed interface Shape {

    /**
     * Checks whether the given point is inside the shape, **relative** to the shape's local origin,
     * considering the given shape's direction.
     * The direction is optional since some shapes (like circles) are not direction-dependent.
     * To check for containment in world space use [Placed.contains] instead.
     */
    fun locallyContains(p2D: Position2D, direction: Direction? = null): Boolean
}

/** A circular shape defined by its [radius]. */
data class Circle(val radius: Double) : Shape {
    override fun locallyContains(p2D: Position2D, direction: Direction?): Boolean =
        p2D.let { (x, y) -> x * x + y * y <= radius * radius }
}

/** A rectangular shape defined by its [width], and [height]. */
data class Rectangle(val width: Double, val height: Double) : Shape {
    /** The half-width of the rectangle. */
    val halfWidth = width / 2

    /** The half-height of the rectangle. */
    val halfHeight = height / 2

    override fun locallyContains(p2D: Position2D, direction: Direction?): Boolean =
        p2D.x in -halfWidth..halfWidth && p2D.y in -halfHeight..halfHeight
}

/**
 * A conical shape representing a sector of a circle (like a vision cone).
 * @property radius the radius of the circle this cone is inscribed in.
 * @property fovDegrees the Field Of View, i.e., the full angular width of the cone in degrees (e.g., 90 for 90° fov).
 */
data class Cone(val radius: Double, val fovDegrees: Degrees) : Shape {
    override fun locallyContains(p2D: Position2D, direction: Direction?): Boolean {
        requireNotNull(direction) { "Cannot verify if a position is inside the cone without direction." }
        val toPoint = p2D.asVector2D().normalized() ?: return true
        val cosAngle = direction.dot(toPoint)
        val cosHalfFOV = cos(Math.toRadians(fovDegrees.value / 2.0))
        return Circle(radius).locallyContains(p2D) && cosAngle >= cosHalfFOV
    }
}

/** A hollow circular shape defined by its [innerRadius] and [outerRadius]. */
data class HollowCircle(val innerRadius: Double, val outerRadius: Double) : Shape {
    init {
        require(innerRadius < outerRadius) { "Inner radius must be less than outer radius." }
    }

    override fun locallyContains(p2D: Position2D, direction: Direction?): Boolean =
        p2D.let { (x, y) -> x * x + y * y in (innerRadius * innerRadius)..(outerRadius * outerRadius) }
}

/** Creates a [Placed] shape at the given position. */
infix fun <S : Shape> S.at(position: Position2D): Placed<S> = Placed(this, position)

/** A [shape] placed in the world, i.e., with a specific [position] and, possibly, a [direction]. */
data class Placed<S : Shape>(val shape: S, var position: Position2D, var direction: Direction? = null) {

    /** Check whether the given point lies withing this placed shape. */
    operator fun contains(point: Position2D): Boolean = shape.locallyContains(point - position, direction)

    /** Update the position and direction of this placed shape. */
    fun update(position: Position2D, direction: Direction? = null) {
        this.position = position
        this.direction = direction
    }
}

/** Check if a [Placed] collides with a container [Shape]. */
infix fun Placed<out Shape>.collidesWith(shape: Shape): Boolean {
    return !this.isFullyContainedIn(shape)
}

/** Check if a [Placed] is entirely contained in a [Shape]. */
fun Placed<out Shape>.isFullyContainedIn(other: Shape): Boolean {
    return when (val shape = this.shape) {
        is Rectangle -> {
            listOf(
                Position2D(-shape.halfWidth, -shape.halfHeight),
                Position2D(shape.halfWidth, -shape.halfHeight),
                Position2D(-shape.halfWidth, shape.halfHeight),
                Position2D(shape.halfWidth, shape.halfHeight),
            ).map { it + this.position }.all { other.locallyContains(it) }
        }
        is Circle -> {
            val directions = listOf(
                Position2D(1.0, 0.0),
                Position2D(-1.0, 0.0),
                Position2D(0.0, 1.0),
                Position2D(0.0, -1.0),
                Position2D(1.0, 1.0),
                Position2D(-1.0, 1.0),
                Position2D(1.0, -1.0),
                Position2D(-1.0, -1.0),
            )
            directions.map { dir ->
                val norm = dir.asVector2D().normalized() ?: zero
                val testPoint = this.position + norm * shape.radius
                testPoint
            }.all { other.locallyContains(it) }
        }
        is Cone -> {
            val direction = this.direction ?: error("Direction must be specified for a placed Cone.")
            val fovRad = Math.toRadians(shape.fovDegrees.value)
            val halfFov = fovRad / 2.0
            val baseAngle = atan2(direction.y, direction.x)
            val radius = shape.radius
            val apex = this.position
            val angles = listOf(baseAngle - halfFov, baseAngle + halfFov, baseAngle)
            val arcPoints = angles.map { angle ->
                Position2D(
                    x = apex.x + radius * cos(angle),
                    y = apex.y + radius * sin(angle),
                )
            }
            val allPoints = listOf(apex) + arcPoints
            allPoints.all { other.locallyContains(it) }
        }
        else -> error("Not implemented for ${shape::class.simpleName}")
    }
}

internal infix fun Placed<Circle>.circleIntersect(other: Placed<Circle>): Boolean {
    val extendedCircle = Circle(this.shape.radius + other.shape.radius)
    return extendedCircle.locallyContains(other.position - this.position)
}

internal infix fun Placed<Circle>.circleRectIntersect(other: Placed<Rectangle>): Boolean {
    val relativeCenter = this.position - other.position
    val closest = Position2D(
        x = relativeCenter.x.coerceIn(-other.shape.halfWidth, other.shape.halfWidth),
        y = relativeCenter.y.coerceIn(-other.shape.halfHeight, other.shape.halfHeight),
    )
    return this.shape.locallyContains(relativeCenter - closest)
}

internal infix fun Placed<Rectangle>.rectIntersect(other: Placed<Rectangle>): Boolean {
    val deltaPosition = this.position - other.position
    val sumHalfWidth = this.shape.halfWidth + other.shape.halfWidth
    val sumHalfHeight = this.shape.halfHeight + other.shape.halfHeight
    return Rectangle(sumHalfWidth * 2, sumHalfHeight * 2).locallyContains(deltaPosition)
}

/** Generate a random position within the given [bounds]. */
fun positionWithin(bounds: Placed<Shape>): Position2D = when (val shape = bounds.shape) {
    is Rectangle -> Position2D(
        x = Random.nextDouble(bounds.position.x - (shape.width / 2), bounds.position.x + (shape.width / 2)),
        y = Random.nextDouble(bounds.position.y - (shape.height / 2), bounds.position.y + (shape.height / 2)),
    )
    is Circle -> {
        val angle = Random.nextDouble(0.0, 2 * PI)
        val radius = shape.radius * sqrt(Random.nextDouble())
        Position2D(
            x = bounds.position.x + radius * cos(angle),
            y = bounds.position.y + radius * sin(angle),
        )
    }
    is HollowCircle -> {
        val angle = Random.nextDouble(0.0, 2 * PI)
        val innerSquared = shape.innerRadius * shape.innerRadius
        val outerSquared = shape.outerRadius * shape.outerRadius
        val radius = sqrt(Random.nextDouble(innerSquared, outerSquared))
        Position2D(
            x = bounds.position.x + radius * cos(angle),
            y = bounds.position.y + radius * sin(angle),
        )
    }
    else -> TODO("No position generation for $shape yet.")
}
