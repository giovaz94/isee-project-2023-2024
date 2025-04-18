package io.github.evasim.model

/** A two-dimensional position.
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
