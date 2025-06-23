package io.github.evasim.model

import io.github.evasim.utils.Rnd
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

/** A position in a two-dimensional space. */
interface Position2D {
    /** The x-axis coordinate (abscissa). */
    val x: Double

    /** The y-axis coordinate (ordinate). */
    val y: Double

    /** The x-axis coordinate (abscissa). */
    operator fun component1(): Double = x

    /** The y-axis coordinate (ordinate). */
    operator fun component2(): Double = y

    /** Adds the coordinates of this position to the coordinates of another position. */
    operator fun plus(p2D: Position2D): Position2D

    /** Returns a new position translated by the given displacement vector. */
    operator fun plus(v2D: Vector2D): Position2D

    /** Subtracts the coordinates of another position from the coordinates of this position. */
    operator fun minus(p2D: Position2D): Position2D

    /** Returns a new position translated in the opposite direction by the given displacement vector. */
    operator fun minus(v2D: Vector2D): Position2D

    /** Position2D factory methods. */
    companion object {
        /** Creates a [Position2D] from a pair of [Double] values. */
        fun from(pair: Pair<Double, Double>): Position2D = Position2D(pair.first, pair.second)

        /** Creates a [Position2D] from the given coordinates. */
        operator fun invoke(x: Double, y: Double): Position2D = Position2DImpl(x, y)
    }
}

private data class Position2DImpl(override val x: Double, override val y: Double) : Position2D {
    override operator fun plus(p2D: Position2D): Position2D = Position2D(x + p2D.x, y + p2D.y)
    override operator fun plus(v2D: Vector2D): Position2D = Position2D(x + v2D.x, y + v2D.y)
    override operator fun minus(p2D: Position2D): Position2D = Position2D(x - p2D.x, y - p2D.y)
    override operator fun minus(v2D: Vector2D): Position2D = Position2D(x - v2D.x, y - v2D.y)
}

/** A vector in a two-dimensional space. */
interface Vector2D {

    /** The horizontal (x-axis) component of the vector. */
    val x: Double

    /** The vertical (y-axis) component of the vector. */
    val y: Double

    /** The horizontal (x-axis) component of the vector. */
    operator fun component1(): Double = x

    /** The vertical (y-axis) component of the vector. */
    operator fun component2(): Double = y

    /** Adds the coordinates of this vector to the coordinates of another vector. */
    operator fun plus(v2D: Vector2D): Vector2D

    /** Subtracts the coordinates of another vector from the coordinates of this vector. */
    operator fun minus(v2D: Vector2D): Vector2D

    /** Multiplies the coordinates of this vector by a scalar. */
    operator fun times(scalar: Double): Vector2D

    /** Divides the coordinates of this vector by a scalar. */
    operator fun div(scalar: Double): Vector2D

    /** Return the inverse of the [Vector2D]. */
    operator fun unaryMinus(): Vector2D

    /** Computes the magnitude of this vector. */
    fun magnitude(): Double

    /** Computes the normalized vector, i.e., the versor if this is not the zero vector, otherwise null. */
    fun normalized(): Versor2D?

    /** Computes the dot product (also known as scalar product) between this vector and the given one. */
    infix fun dot(v2D: Vector2D): Double

    /** Returns whether this vector is a zero vector (both components are zero). */
    fun isZero(): Boolean

    /** Inverts and randomly rotates this vector within the given angle range (in degrees). */
    fun invertedWithRandomAngle(minDegrees: Double, maxDegrees: Double): Vector2D

    /** Useful factory methods. */
    companion object {
        /** The 2D zero vector. */
        val ZERO: Vector2D = Vector2D(0.0, 0.0)

        /** Creates a [Vector2D] from a pair of [Double] values. */
        fun from(pair: Pair<Double, Double>): Vector2D = Vector2D(pair.first, pair.second)

        /** Creates a [Vector2D] from the given components. */
        operator fun invoke(x: Double, y: Double): Vector2D = Vector2DImpl(x, y)
    }
}

private data class Vector2DImpl(override val x: Double, override val y: Double) : Vector2D {
    override operator fun plus(v2D: Vector2D): Vector2D = Vector2D(x + v2D.x, y + v2D.y)
    override operator fun minus(v2D: Vector2D): Vector2D = Vector2D(x - v2D.x, y - v2D.y)
    override operator fun times(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)
    override operator fun div(scalar: Double): Vector2D = Vector2D(x / scalar, y / scalar)
    override operator fun unaryMinus(): Vector2D = Vector2D(-x, -y)
    override fun isZero(): Boolean = x == 0.0 && y == 0.0
    override fun invertedWithRandomAngle(minDegrees: Double, maxDegrees: Double): Vector2D {
        require(minDegrees <= maxDegrees) { "minDegrees must be <= maxDegrees" }

        val inverted = -this
        val randomAngleDegrees = Rnd.nextDouble(minDegrees, maxDegrees)
        val randomAngleRadians = Math.toRadians(randomAngleDegrees)

        val cosA = cos(randomAngleRadians)
        val sinA = sin(randomAngleRadians)

        val rotatedX = inverted.x * cosA - inverted.y * sinA
        val rotatedY = inverted.x * sinA + inverted.y * cosA

        return Vector2D(rotatedX, rotatedY)
    }

    override fun magnitude(): Double = hypot(x, y)
    override fun normalized(): Versor2D? = takeUnless { it.isZero() }?.let { Versor2D.from(it / it.magnitude()) }
    override infix fun dot(v2D: Vector2D): Double = x * v2D.x + y * v2D.y
}

@JvmInline
/**
 * A 2D angle in degrees.
 * @property value The angle in degrees.
 */
value class Degrees(val value: Double) {
    override fun toString(): String = "$value°, ${Math.toRadians(value)} rad"
}

@JvmInline
/**
 * A 2D angle in radians.
 * @property value The angle in radians.
 */
value class Radians(val value: Double) {
    override fun toString(): String = "$value rad, ${Math.toDegrees(value)}°"
}

/** A direction in a two-dimensional space as a [Versor2D]. */
typealias Direction = Versor2D

/**
 * A unit vectors, also known as versors, in a two-dimensional space.
 * It guarantees that the vector has a magnitude of 1.
 */
interface Versor2D : Vector2D {

    /** Vector2D factory methods. */
    companion object {
        /**
         * Creates a [Versor2D] from a [Vector2D].
         * It Should be a unit vector, i.e., magnitude = 1, otherwise it will throw an error.
         */
        fun from(vector: Vector2D): Versor2D = Versor2D(vector.x, vector.y)

        /**
         * Creates a [Versor2D] from the given components.
         * It should be a unit vector, i.e., magnitude = 1, otherwise it will throw an error.
         */
        operator fun invoke(x: Double, y: Double): Versor2D = Versor2DImpl(Vector2D(x, y))

        /** Creates a [Versor2D] that points to the right. */
        val RIGHT = Versor2D(x = 1.0, y = 0.0)

        /** Creates a [Versor2D] that points to the top. */
        val UP = Versor2D(x = 0.0, y = 1.0)

        /** Creates a [Versor2D] that points to the left. */
        val LEFT = Versor2D(x = -1.0, y = 0.0)

        /** Creates a [Versor2D] that points to the bottom. */
        val DOWN = Versor2D(x = 0.0, y = -1.0)

        /** Creates a [Versor2D] from an angle in degrees. */
        fun fromAngle(degrees: Degrees): Versor2D = fromAngle(Radians(Math.toRadians(degrees.value)))

        /** Creates a [Versor2D] from an angle in radians. */
        fun fromAngle(radians: Radians): Versor2D = Versor2D(cos(radians.value), sin(radians.value))
    }
}

private data class Versor2DImpl(val vector: Vector2D) : Versor2D, Vector2D by vector {
    init {
        vector.magnitude().let {
            require(abs(it - 1.0) < EPSILON) {
                "The vector must be a unit vector (magnitude = 1), but was $it."
            }
        }
    }

    private companion object {
        private const val EPSILON = 1e-7
    }
}

/** Converts a 2D position in a 2D vector. */
fun Position2D.asVector2D(): Vector2D = Vector2D(x, y)

/** Computes the distance between two entities. */
fun Entity.distanceTo(other: Entity): Double = (position - other.position).let { d -> sqrt(d.x * d.x + d.y * d.y) }

/** The origin of the coordinate system. */
val origin: Position2D = Position2D(0.0, 0.0)

/** A zero vector in a 2D space, i.e., a vector with both components equal to zero. */
val zero: Vector2D = Vector2D(0.0, 0.0)
