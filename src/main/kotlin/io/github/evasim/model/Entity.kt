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
}
