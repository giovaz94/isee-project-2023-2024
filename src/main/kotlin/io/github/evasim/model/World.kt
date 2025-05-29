package io.github.evasim.model

import io.github.evasim.controller.EventBusPublisher
import io.github.evasim.controller.EventPublisher
import io.github.evasim.controller.UpdatedBlob
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.time.Duration

/**
 * Represents the simulation world that encompasses all entities, foods, and blobs
 * and defines the overall structure and behavior of the environment.
 */
interface World : EventPublisher {

    /**
     * Defines the overall structure or boundaries of the simulation world,
     * representing its shape relative to its local origin.
     */
    val shape: Shape

    /** A sequence of all food items in the simulation world. */
    val foods: Sequence<Food>

    /** A sequence of all blob entities in the simulation world. */
    val blobs: Sequence<Blob>

    /**
     * Represents the sequence of [SpawnZone] within the world simulation, where blobs can be generated.
     * Each spawn zone is characterized by a shape that determines its spatial boundaries.
     */
    val spawnZones: Sequence<SpawnZone>

    /** Adds the given [food] item to this world. */
    fun addFood(food: Food)

    /** Removes the specified [food] item from this world. */
    fun removeFood(food: Food)

    /** Adds a new blob to this world. */
    fun addBlob(blob: Blob)

    /** Retrieves an entity by its [id], returning a possibly nullable type. */
    operator fun get(id: Entity.Id): Entity?

    /** Adds a new spawn zone to this world. */
    fun addSpawnZone(spawnZone: SpawnZone)

    /**
     * Updates the state of the world based on the given elapsed time.
     * @param elapsed The duration of time that has passed since the last update.
     */
    fun update(blobId: Entity.Id, elapsed: Duration)

    /** Provides companion object methods for managing and creating `World` instances. */
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
                .associateBy { it.id }
                .toMap(ConcurrentHashMap())
            val blobs = spawnZones
                .flatMap { zone -> generateSequence { positionWithin(zone.place) }.take(blobsPerSpawnZone) }
                .mapIndexed { i, p ->
                    Blob(id = Entity.Id("blob-$i"), personality = if (i < hawkyBlobs) Hawk else Dove, position = p)
                }
                .associateBy { it.id }
                .toMap(ConcurrentHashMap())
            WorldImpl(shape, foods, blobs, CopyOnWriteArraySet(spawnZones))
        }
    }
}

private data class WorldImpl(
    override val shape: Shape,
    private val worldFoods: ConcurrentHashMap<Entity.Id, Food>,
    private val worldBlobs: ConcurrentHashMap<Entity.Id, Blob>,
    private val worldSpawnZones: CopyOnWriteArraySet<SpawnZone>,
) : World, EventBusPublisher() {

    override val foods: Sequence<Food> = worldFoods.values.asSequence()

    override val blobs: Sequence<Blob> = worldBlobs.values.asSequence()

    override val spawnZones: Sequence<SpawnZone> = worldSpawnZones.asSequence()

    override fun addFood(food: Food) {
        worldFoods[food.id] = food
    }

    override fun removeFood(food: Food) {
        worldFoods.remove(food.id)
    }

    override fun addBlob(blob: Blob) {
        worldBlobs[blob.id] = blob
    }

    override fun addSpawnZone(spawnZone: SpawnZone) {
        worldSpawnZones.add(spawnZone)
    }

    override fun get(id: Entity.Id): Entity? = worldBlobs[id] ?: worldFoods[id]

    override fun update(blobId: Entity.Id, elapsed: Duration) {
        worldBlobs[blobId]?.let { blob ->
            blob.update(elapsed)
            post(UpdatedBlob(blob.clone()))
        }
    }
}
