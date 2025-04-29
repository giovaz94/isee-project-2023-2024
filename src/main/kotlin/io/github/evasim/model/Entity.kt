package io.github.evasim.model

/** An interface modeling a generic entity in the simulation. */
interface Entity {

    /**
     * The value object identifying of an [Entity].
     * @property value the unique identifier of the entity.
     */
    @JvmInline
    value class Id(val value: String)

    /** The unique identifier of the entity. */
    val id: Id

    /** The position where the entity is located in the simulation space. */
    val position: Position2D

    /** The shape of the entity in the simulation space. */
    val shape: Shape

    /**
     * Check whether this entity is colliding with another entity.
     * Expected usage:
     * ```
     * if (entity collidingWith otherEntity) {
     *   // Handle collision
     * }
     * ```
     */
    infix fun collidingWith(other: Entity): Boolean {
        val myShape = this.shape
        val otherShape = other.shape
        return when (myShape) {
            is Circle -> when (otherShape) {
                is Circle -> myShape at position circleIntersect (otherShape at other.position)
                is Rectangle -> myShape at position circleRectIntersect (otherShape at other.position)
                is Cone -> TODO("No collisions with cones yet")
            }
            is Rectangle -> when (otherShape) {
                is Circle -> otherShape at other.position circleRectIntersect (myShape at position)
                is Rectangle -> myShape at position rectIntersect (otherShape at other.position)
                is Cone -> TODO("No collisions with cones yet")
            }
            is Cone -> TODO("No collisions with cones yet")
        }
    }
}
