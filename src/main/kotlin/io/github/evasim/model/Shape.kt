package io.github.evasim.model

/** A shape in a two-dimensional space. */
sealed interface Shape {

    /** Checks if the given point is inside this shape. */
    operator fun contains(p2D: Position2D): Boolean
}

/** A circular shape defined by its [center] and [radius]. */
data class Circle(val center: Position2D, val radius: Double) : Shape {
    override fun contains(p2D: Position2D): Boolean = (p2D - center).let { (x, y) -> x * x + y * y <= radius * radius }
}

/** A rectangular shape defined by its [topLeft] corner, [width], and [height]. */
data class Rectangle(val topLeft: Position2D, val width: Double, val height: Double) : Shape {
    override fun contains(p2D: Position2D): Boolean =
        p2D.x in topLeft.x..(topLeft.x + width) && p2D.y in topLeft.y..(topLeft.y + height)
}

/** Checks if this shape intersects with another shape. */
fun Shape.intersect(other: Shape): Boolean = when (this) {
    is Circle -> when (other) {
        is Circle -> intersect(other)
        is Rectangle -> intersect(other)
    }
    is Rectangle -> when (other) {
        is Circle -> other.intersect(this)
        is Rectangle -> intersect(other)
    }
}

internal fun Circle.intersect(other: Circle): Boolean {
    val distanceSquared = (this.center - other.center).let { (x, y) -> x * x + y * y }
    val radiusSum = this.radius + other.radius
    return distanceSquared <= radiusSum * radiusSum
}

internal fun Circle.intersect(other: Rectangle): Boolean {
    val closestX = other.topLeft.x.coerceIn(center.x - radius, center.x + radius)
    val closestY = other.topLeft.y.coerceIn(center.y - radius, center.y + radius)
    return (closestX - center.x).let { x -> (closestY - center.y).let { y -> x * x + y * y <= radius * radius } }
}

internal fun Rectangle.intersect(other: Rectangle): Boolean {
    val overlapX = !(topLeft.x + width < other.topLeft.x || other.topLeft.x + other.width < topLeft.x)
    val overlapY = !(topLeft.y + height < other.topLeft.y || other.topLeft.y + other.height < topLeft.y)
    return overlapX && overlapY
}
