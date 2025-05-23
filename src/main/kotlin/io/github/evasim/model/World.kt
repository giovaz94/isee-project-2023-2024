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
         * @param shape The shape of the world positioned at the origin, i.e., (0, 0).
         * @param spawnZones The spawn zones within the world.
         * @param blobsAmount The total number of blobs in the world.
         * @param hawkyBlobs The number of hawky blobs in the world.
         * @param foodsAmount The number of food items in the world.
         */
        data class Configuration(
            val shape: Shape,
            val spawnZones: Set<SpawnZone>,
            val blobsAmount: Int,
            val hawkyBlobs: Int,
            val foodsAmount: Int = blobsAmount,
        )

        /**
         * Creates a new world instance based on the provided [configuration].
         */
        fun fromConfiguration(configuration: Configuration): World = with(configuration) {
            val blobsPerSpawnZone = blobsAmount / spawnZones.size
            val foods = generateSequence { positionWithin(shape at origin) }
                .filter { pos -> spawnZones.none { pos in it.place } }
                .take(foodsAmount)
                .map { Food.of(Circle(radius = 10.0), it, pieces = 2) }
                .toSet()
            val blobs = spawnZones
                .flatMap { zone -> generateSequence { positionWithin(zone.place) }.take(blobsPerSpawnZone) }
                .mapIndexed { i, p ->
                    Blob(
                        id = Entity.Id("blob-$i"),
                        personality = if (i < hawkyBlobs) Hawk else Dove,
                        position = p,
                        velocity = (origin - p).asVector2D().normalized()?.times(scalar = 20.0) ?: zero,
                    )
                }
                .toSet()
            WorldImpl(shape, foods, blobs, spawnZones)
        }
    }
}

internal data class WorldImpl(
    override val shape: Shape,
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
