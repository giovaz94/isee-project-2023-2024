package io.github.evasim.model

/** An interface modeling a generic entity in the simulation. */
interface Entity {

    /** The unique identifier of the entity. */
    val id: EntityId

    /** The position where the entity is located in the simulation space. */
    val position: Position2D

    /** The shape of the entity in the simulation space. */
    val shape: Shape
}

/** The value object identifier of an [Entity]. */
interface EntityId {

    /** The unique identifier of the entity. */
    val value: String

    /** EntityId factory methods. */
    companion object {
        /** Creates a new [EntityId] with the given id. */
        operator fun invoke(id: String): EntityId = object : EntityId {
            override val value: String = id
        }
    }
}
