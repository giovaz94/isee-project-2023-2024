package io.github.evasim.model

import kotlin.math.hypot

/**
 * A position in a two-dimensional space.
 * @param x the x-axis coordinate (abscissa)
 * @param y the y-axis coordinate (ordinate)
 */
data class Position2D(val x: Double, val y: Double) {
    /** Adds the coordinates of this position to the coordinates of another position. */
    operator fun plus(p2D: Position2D): Position2D = Position2D(x + p2D.x, y + p2D.y)

    /** Subtracts the coordinates of another position from the coordinates of this position. */
    operator fun minus(p2D: Position2D): Position2D = Position2D(x - p2D.x, y - p2D.y)

    /** Useful factory methods. */
    companion object {
        /** Creates a [Position2D] from a pair of [Double] values. */
        fun from(pair: Pair<Double, Double>): Position2D = Position2D(pair.first, pair.second)
    }
}

/**
 * A vector in a two-dimensional space.
 * @param x the horizontal (x-axis) coordinate
 * @param y the vertical (y-axis) coordinate
 */
data class Vector2D(val x: Double, val y: Double) {

    /** Adds the coordinates of this vector to the coordinates of another vector. */
    operator fun plus(v2D: Vector2D): Vector2D = Vector2D(x + v2D.x, y + v2D.y)

    /** Subtracts the coordinates of another vector from the coordinates of this vector. */
    operator fun minus(v2D: Vector2D): Vector2D = Vector2D(x - v2D.x, y - v2D.y)

    /** Multiplies the coordinates of this vector by a scalar. */
    operator fun times(scalar: Double): Vector2D = Vector2D(x * scalar, y * scalar)

    /** Divides the coordinates of this vector by a scalar. */
    operator fun div(scalar: Double): Vector2D = Vector2D(x / scalar, y / scalar)

    /** Computes the magnitude of this vector. */
    fun magnitude(): Double = hypot(x, y)

    /** Computes the normalized vector, i.e., the versor. */
    fun normalized(): Vector2D = magnitude().let { if (it != 0.0) this / it else Vector2D(0.0, 0.0) }

    /** Useful factory methods. */
    companion object {
        /** Creates a [Vector2D] from a pair of [Double] values. */
        fun from(pair: Pair<Double, Double>): Vector2D = Vector2D(pair.first, pair.second)
    }
}
