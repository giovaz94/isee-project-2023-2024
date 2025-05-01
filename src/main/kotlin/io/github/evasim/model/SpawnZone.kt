package io.github.evasim.model

/**
 * Defines a zone where entities can spawn in a simulation.
 *
 * A `SpawnZone` is characterized by a defined two-dimensional shape, described in local coordinates,
 * that determines the boundary within which spawning can occur. This does not include the placement
 * or position of the zone in the world; it operates within its own local coordinate space.
 */
interface SpawnZone {

    /**
     * The shape defining the boundaries of the spawn zone.
     *
     * Represents a two-dimensional area in local coordinates. The specific implementation of the shape
     * (e.g., circle, rectangle, etc.) determines how the area is defined and used. The shape does not
     * provide world positioning; to place it in the world, it must be linked to a specific location.
     *
     * Can be utilized to check if certain points (relative to the origin) are inside the defined area,
     * depending on the shape's specific characteristics.
     */
    val shape: Shape
}
