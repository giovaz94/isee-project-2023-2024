package io.github.evasim.model

/**
 * Defines a zone where entities can spawn in a simulation.
 * A `SpawnZone` is characterized by a defined two-dimensional shape
 * that determines the boundary within which spawning can occur.
 */
interface SpawnZone {

    /**
     * The shape defining the boundaries of the spawn zone.
     * Represents a two-dimensional area in local coordinates.
     * The specific implementation of the shape (e.g., circle, rectangle, etc.) determines how the area
     * is defined and used.
     */
    val shape: Shape

    /**
     * The position of the spawn zone in the world.
     */
    val position: Position2D

    /**
     * The shape of the spawn zone placed in world coordinates.
     */
    val place: Placed<Shape>
        get() = shape at position

    /** SpawnZone factory methods. */
    companion object {
        /** Creates a new spawn zone with the specified [shape] and [position]. */
        operator fun invoke(shape: Shape, position: Position2D): SpawnZone = SpawnZoneImpl(shape, position)
    }
}

internal data class SpawnZoneImpl(override val shape: Shape, override val position: Position2D) : SpawnZone
