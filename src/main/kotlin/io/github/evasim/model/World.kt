package io.github.evasim.model

/**
 * Represents the simulation world that encompasses all entities, foods, and blobs
 * and defines the overall structure and behavior of the environment.
 */
interface World {
    /**
     * Defines the overall structure or boundaries of the simulation world,
     * representing its shape relative to its local origin.
     * The shape does not contain positional data within the world, as placement
     * is determined separately.
     */
    val shape: Shape

    /**
     * A sequence of all entities present in the simulation world.
     *
     * The `entities` sequence provides access to every `Entity` in the simulation,
     * including both dynamic and static ones. Each `Entity` represents an independent
     * element of the simulation, such as `Blob`s, `Food`, or other interactable objects.
     *
     * This sequence is typically used for iterating over all entities to simulate interactions,
     * detect collisions, or update the state of the world and its contents.
     */
    val entities: Sequence<Entity>

    /**
     * A sequence of all food items in the simulation world.
     *
     * The `foods` sequence provides access to every `Food` entity present in the simulation world.
     * Each `Food` represents a collectable energy source for entities, such as `Blob`s. The sequence
     * allows iterative access to all existing `Food` instances, supporting lazy processing and
     * efficient memory usage for large simulations.
     *
     * This sequence is typically used to simulate interactions between entities and the available
     * food in the environment, such as attempting to collect or consume it.
     */
    val foods: Sequence<Food>

    /**
     * A sequence of all blob entities in the simulation world.
     *
     * The `blobs` sequence provides access to every `Blob` present in the simulation world. Each `Blob`
     * is an entity with physical properties such as position, velocity, health, and sight. Blobs have
     * behaviors like responding to external forces, moving, and interacting with the environment or
     * other entities. This sequence supports lazy evaluation, enabling efficient iteration over large
     * numbers of blob instances.
     *
     * This sequence is primarily used for simulation updates, analysis, or interaction handling within
     * the world, including movement computations, collision detection, or evaluating interactions such
     * as pursuit or reproduction.
     */
    val blobs: Sequence<Blob>

    /**
     * Represents a sequence of spawn zones within the world simulation.
     * Spawn zones define specific areas where entities like food or blobs can appear or be generated.
     *
     * Each spawn zone is characterized by a shape that determines its spatial boundaries.
     */
    val spawnZones: Sequence<SpawnZone>

    /**
     * Adds a new entity to the world and returns an updated world instance.
     */
    fun addEntity(entity: Entity): World

    /**
     * Adds a new food item to the world and returns an updated world instance.
     */
    fun addFood(food: Food): World

    /**
     * Adds a new blob to the world and returns an updated world instance.
     */
    fun addBlob(blob: Blob): World

    /**
     * Adds a new spawn zone to the world and returns an updated world instance.
     */
    fun addSpawnZone(spawnZone: SpawnZone): World

    /**
     * Provides companion object methods for managing and creating `World` instances.
     */
    companion object {
        /**
         * Creates a new instance of the `World` using the specified parameters.
         *
         * @param shape the shape defining the boundaries of the world.
         * @param entities a sequence of entities that exist in the world.
         * @param foods a sequence of food items present in the world.
         * @param blobs a sequence of blobs present in the world.
         * @param spawnZones a sequence of spawn zones within the*/
        fun invoke(
            shape: Shape,
            entities: Sequence<Entity>,
            foods: Sequence<Food>,
            blobs: Sequence<Blob>,
            spawnZones: Sequence<SpawnZone>,
        ): World = WorldImpl(shape, entities, foods, blobs, spawnZones)

        /**
         * Creates an empty world with no entities, foods, blobs, or spawn zones.
         *
         * An empty world is defined by a given shape and contains no interactive elements.
         * It provides a clean slate for initializing a simulation environment.
         *
         * @param shape The shape of the world to use for the empty instance.
         * @return A new instance of the `World` interface with no entities.
         */
        fun empty(shape: Shape): World = WorldImpl(
            shape = shape,
            entities = emptySequence(),
            foods = emptySequence(),
            blobs = emptySequence(),
            spawnZones = emptySequence(),
        )
    }
}

/**
 * Implementation of the `World` interface that models a simulation world
 * containing entities, food items, blobs, and spawn zones within a specific shape.
 *
 * @property shape the shape that defines the boundaries of this world.
 * @property entities the collection of entities present in the world.
 * @property foods the collection of food items available in the world.
 * @property blobs the collection of blobs interacting within the world.
 * @property spawnZones the collection of spawn zones where entities can appear.
 */
internal data class WorldImpl(
    override val shape: Shape,
    override val entities: Sequence<Entity>,
    override val foods: Sequence<Food>,
    override val blobs: Sequence<Blob>,
    override val spawnZones: Sequence<SpawnZone>,
) : World {

    /**
     * Adds an entity to the world and returns a new updated world instance.
     *
     * @param entity the entity to be added to the world.
     * @return a new instance of the world with the added entity included.
     */
    override fun addEntity(entity: Entity): World = copy(entities = entities + entity)

    /**
     * Adds a new food item to the world and returns an updated world instance.
     *
     * @param food the food item to be added to the world.
     * @return a new instance of the world with the added food item included.
     */
    override fun addFood(food: Food): World = copy(foods = foods + food)

    /**
     * Adds a new blob to the world and returns an updated world instance.
     *
     * @param blob the blob to be added to the world.
     * @return a new instance of the world with the added blob included.
     */
    override fun addBlob(blob: Blob): World = copy(blobs = blobs + blob)

    /**
     * Adds a new spawn zone to the world and returns an updated world instance.
     *
     * @param spawnZone the spawn zone to be added, defining an area within the world
     *                  where entities can be generated or appear.
     * @return a new instance of the world with the additional spawn zone included.
     */
    override fun addSpawnZone(spawnZone: SpawnZone): World = copy(spawnZones = spawnZones + spawnZone)
}
