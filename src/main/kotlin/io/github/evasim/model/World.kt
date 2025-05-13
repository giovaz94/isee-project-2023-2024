package io.github.evasim.model

import kotlin.time.Duration

/**
 * Represents the simulation world that encompasses all entities, foods, and blobs
 * and defines the overall structure and behavior of the environment.
 */
interface World {
    /**
     * Defines the overall structure or boundaries of the simulation world,
     * representing its shape relative to its local origin.
     */
    val shape: Shape

    /**
     * The position of the World in the 2D space.
     */
    val position: Position2D

    /**
     * A sequence of all food items in the simulation world.
     */
    val foods: Sequence<Food>

    /**
     * A sequence of all blob entities in the simulation world.
     */
    val blobs: Sequence<Blob>

    /**
     * Represents a sequence of spawn zones within the world simulation.
     * Spawn zones define specific areas where entities like food or blobs can appear or be generated.
     * Each spawn zone is characterized by a shape that determines its spatial boundaries.
     */
    val spawnZones: Sequence<SpawnZone>

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
     * Updates the state of the world based on the given elapsed time.
     *
     * @param elapsed The duration of time that has passed since the last update.
     * @return A new instance of the world with the updated state.
     */
    fun update(elapsed: Duration): World

    /**
     * Provides companion object methods for managing and creating `World` instances.
     */
    companion object {

        /**
         * Configuration class for creating a new world instance.
         * @param shape The shape of the world.
         * @param spawnZones The spawn zones within the world.
         * @param blobsAmount The total number of blobs in the world.
         * @param hawkyBlobs The number of hawky blobs in the world.
         * @param foodsAmount The number of food items in the world.
         * @param position The position of the world in 2D space.
         */
        data class Configuration(
            val shape: Shape,
            val spawnZones: Set<SpawnZone>,
            val blobsAmount: Int,
            val hawkyBlobs: Int,
            val foodsAmount: Int = blobsAmount,
            val position: Position2D = when (shape) {
                is Rectangle -> Position2D(x = origin.x + shape.width / 2, y = origin.y + shape.height / 2)
                is Circle -> Position2D(x = origin.x + shape.radius, y = origin.y + shape.radius)
                else -> TODO()
            },
        )

        /**
         * Creates a new world instance based on the provided [configuration].
         */
        fun fromConfiguration(configuration: Configuration): World = with(configuration) {
            val blobsPerSpawnZone = blobsAmount / spawnZones.size
            val foods = generateSequence { positionWithin(shape at position) }
                .filter { pos -> spawnZones.none { pos in it.placedShape } }
                .take(foodsAmount)
                .map { Food.of(Circle(radius = 10.0), it, 2) }
                .toSet()
            val blobs = spawnZones
                .flatMap { zone -> generateSequence { positionWithin(zone.placedShape) }.take(blobsPerSpawnZone) }
                .mapIndexed { i, p -> Blob(Entity.Id("blob-$i"), if (i < hawkyBlobs) Hawk else Dove, p) }
                .toSet()
            WorldImpl(shape, position, foods, blobs, spawnZones)
        }
    }
}

internal data class WorldImpl(
    override val shape: Shape,
    override val position: Position2D,
    private val worldFoods: Set<Food>,
    private val worldBlobs: Set<Blob>,
    private val worldSpawnZones: Set<SpawnZone>,
) : World {

    override val foods: Sequence<Food> = worldFoods.asSequence()

    override val blobs: Sequence<Blob> = worldBlobs.asSequence()

    override val spawnZones: Sequence<SpawnZone> = worldSpawnZones.asSequence()

    override fun addFood(food: Food): World = copy(worldFoods = worldFoods + food)

    override fun addBlob(blob: Blob): World = copy(worldBlobs = worldBlobs + blob)

    override fun addSpawnZone(spawnZone: SpawnZone): World = copy(worldSpawnZones = worldSpawnZones + spawnZone)

    override fun update(elapsed: Duration): World {
        blobs.forEach { it.update(elapsed) }
        return this
    }
}
