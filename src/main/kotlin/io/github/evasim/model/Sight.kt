package io.github.evasim.model

/** Represents the visual perception capability of an entity within the simulation. */
interface Sight {

    /**
     * The portion of the environment currently visible to the entity.
     * This area determines what the entity can perceive and react to.
     */
    val visibilityArea: Placed<Shape>

    /**
     * Checks whether the given entity is within the sight's field of view.
     * Expected usage:
     * ```kotlin
     * if (food in blob.sight) {
     *   // The entity is within the sight's field of view
     * }
     * ```
     */
    operator fun contains(entity: Entity): Boolean

    /** Updates the sight's pose based on the given position and direction. */
    fun update(position: Position2D, direction: Direction)

    /** The sight's factory methods. */
    companion object {
        /** Creates a new sight with the specified shape and initial pose. */
        operator fun invoke(shape: Shape, initialPosition: Position2D, direction: Direction): Sight =
            SightImpl(Placed(shape, initialPosition, direction))
    }
}

internal data class SightImpl(override val visibilityArea: Placed<Shape>) : Sight {

    override fun update(position: Position2D, direction: Direction) = visibilityArea.update(position, direction)

    override fun contains(entity: Entity): Boolean = entity.position in visibilityArea
}
