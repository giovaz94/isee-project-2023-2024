package io.github.evasim.model

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Represents the simulation world that encompasses all entities, foods, and blobs
 * and defines the overall structure and behavior of the environment.
 */
interface World : EventPublisher {

    /** Defines the structure/boundary of the simulation world, representing its shape relative to the origin. */
    val shape: Shape

    /** The initial number of food items in the simulation world. */
    val initialFoods: Int

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

    /** Finds the blob with the specified stringified [id] in this world, or returns null if not found. */
    fun findBlob(id: String): Blob?

    /** Finds the blob with the specified [id] in this world, or returns null if not found. */
    fun findBlob(id: Entity.Id): Blob?

    /** Finds the food with the specified stringified [id] in this world, or returns null if not found. */
    fun findFood(id: String): Food?

    /** Finds the food item with the specified [id] in this world, or returns null if not found. */
    fun findFood(id: Entity.Id): Food?

    /** Provides companion object methods for managing and creating `World` instances. */
    companion object {

        /**
         * Configuration class for creating a new world instance.
         * @param shape The shape of the world positioned at the origin, i.e., (0, 0).
         * @param spawnZones The spawn zones within the world.
         * @param hawkyBlobs The number of hawky blobs in the world.
         * @param doveBlobs The number of dove blobs in the world.
         * @param foodsAmount The number of food items in the world.
         */
        data class Configuration(
            val shape: Shape,
            val spawnZones: Set<SpawnZone>,
            val hawkyBlobs: Int,
            val doveBlobs: Int,
            val foodsAmount: Int = (hawkyBlobs + doveBlobs) / 2,
        )

        /** Creates a new world instance based on the provided [configuration]. */
        fun fromConfiguration(configuration: Configuration): World = with(configuration) {
            val blobsPerSpawnZone = (hawkyBlobs + doveBlobs) / spawnZones.size
            val foods = generateFoods(shape, spawnZones, foodsAmount)
            val blobs = spawnZones
                .flatMap { zone -> generateSequence { positionWithin(zone.place) }.take(blobsPerSpawnZone) }
                .mapIndexed { i, p ->
                    Blob(id = Entity.Id("blob-$i"), personality = if (i < hawkyBlobs) Hawk else Dove, position = p)
                }
                .associateBy { it.id }
                .toMap(ConcurrentHashMap())
            WorldImpl(shape, foodsAmount, foods, blobs, CopyOnWriteArraySet(spawnZones))
        }

        /** Creates a new world instance from the given one. */
        fun from(world: World): World = with(world) {
            val blobs = blobs
                .filter { it.isAlive() }
                .flatMap { b ->
                    if (b.canReproduce()) {
                        listOf(
                            b,
                            Blob(id = Entity.Id("blob-${b.id}-I"), personality = b.personality, position = b.position),
                        )
                    } else if (b.isAlive()) {
                        listOf(b)
                    } else {
                        emptyList()
                    }
                }.associateBy { it.id }
                .toMap(ConcurrentHashMap())
            val foods = generateFoods(shape, spawnZones.toSet(), blobs.count() / 2)
            WorldImpl(shape, initialFoods, foods, blobs, CopyOnWriteArraySet(spawnZones.toSet()))
        }

        private fun generateFoods(
            container: Shape,
            spawnZones: Set<SpawnZone>,
            quantity: Int,
        ): ConcurrentHashMap<Entity.Id, Food> {
            val acceptedFoods = mutableSetOf<Placed<Circle>>()
            return generateSequence { positionWithin(container at origin) }
                .filter { pos -> spawnZones.none { pos in it.place } }
                .map { Circle(radius = 10.0) at it }
                .filter { c -> if (acceptedFoods.none { it circleIntersect c }) acceptedFoods.add(c) else false }
                .take(quantity)
                .map { Food.of(it.shape, it.position, pieces = 2) }
                .associateBy { it.id }
                .toMap(ConcurrentHashMap())
        }
    }
}

private data class WorldImpl(
    override val shape: Shape,
    override val initialFoods: Int,
    private val worldFoods: ConcurrentHashMap<Entity.Id, Food>,
    private val worldBlobs: ConcurrentHashMap<Entity.Id, Blob>,
    private val worldSpawnZones: CopyOnWriteArraySet<SpawnZone>,
) : World, EventBusPublisher() {

    override val foods: Sequence<Food> = worldFoods.values.asSequence()

    override val blobs: Sequence<Blob> = worldBlobs.values.asSequence()

    override val spawnZones: Sequence<SpawnZone> = worldSpawnZones.asSequence()

    override fun addFood(food: Food) {
        worldFoods[food.id] = food
        subscribers.forEach { food.register(it) }
    }

    override fun removeFood(food: Food) {
        worldFoods.remove(food.id)
        post(RemoveFood(food))
        subscribers.forEach { food.unregister(it) }
    }

    override fun addBlob(blob: Blob) {
        worldBlobs[blob.id] = blob
        subscribers.forEach { blob.register(it) }
    }

    override fun findBlob(id: String): Blob? = findBlob(Entity.Id(id))

    override fun findBlob(id: Entity.Id): Blob? = worldBlobs[id]

    override fun findFood(id: String): Food? = findFood(Entity.Id(id))

    override fun findFood(id: Entity.Id): Food? = worldFoods[id]

    override fun register(subscriber: EventSubscriber): Boolean = super.register(subscriber).also {
        if (it) {
            (foods + blobs).forEach { e -> e.register(subscriber) }
            post(UpdatedWorld(this))
        }
    }

    override fun unregister(subscriber: EventSubscriber): Boolean = super.unregister(subscriber).also {
        if (it) (foods + blobs).forEach { e -> e.unregister(subscriber) }
    }
}
