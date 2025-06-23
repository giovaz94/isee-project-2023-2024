package io.github.evasim.model

import java.util.Collections
import java.util.concurrent.atomic.AtomicReference

/** A type alias for the energy amount. */
typealias Energy = Double

/** A collectable food item in the simulation. */
interface Food : Entity, EventPublisher {

    /** The smallest food unit that can be collected. */
    interface Piece {
        /** The amount of energy this piece provides if eaten. */
        val energy: Energy

        /** The blob that collected this piece or null if it hasn't been collected yet. */
        fun collectedBy(): Blob?
    }

    /** The total energy provided by all pieces of this food item. */
    val totalEnergy: Energy

    /** The set of pieces that make up this food item. */
    val pieces: Set<Piece>

    /** @return true if this food item has at least one uncollected piece, false otherwise. */
    fun hasUncollectedPieces(): Boolean = pieces.any { it.collectedBy() == null }

    /**
     * Attempts to collect this food item by the given blob and, if successfully,
     * it returns the set of blobs that successfully collected pieces of this food.
     */
    fun attemptCollecting(blob: Blob): Set<Blob>

    /** Food factory methods. */
    companion object {
        /** Creates a new [Food] with the given id, shape, position, and number of pieces. */
        fun of(id: Entity.Id, shape: Shape, position: Position2D, pieces: Int): Food =
            FoodImpl(id, shape, position, pieces)

        /**
         * Creates a new [Food] instance with the specified shape, position, and number of pieces.
         * The [Id]entifier is automatically generated based on the provided position,
         * under the assumption that food can only be placed once at a given location.
         *
         * **Note:** This method does not enforce uniqueness of positions: calling it multiple times with the
         * same position will result in multiple food entities with the same identifier.
         * It is the caller's responsibility to ensure positional uniqueness.
         */
        fun of(shape: Shape, position: Position2D, pieces: Int): Food =
            FoodImpl(Entity.Id("food@${position.x}-${position.y}"), shape, position, pieces)
    }
}

@ConsistentCopyVisibility
private data class FoodImpl private constructor(
    override val id: Entity.Id,
    override val shape: Shape,
    override val position: Position2D,
    private val pieceSet: Set<PieceImpl>,
) : Food, EventBusPublisher() {

    constructor(id: Entity.Id, shape: Shape, position: Position2D, numPieces: Int) :
        this(id, shape, position, pieceSet = (1..numPieces).map { PieceImpl(energy = 1.0) }.toSet())

    override val totalEnergy: Energy = pieceSet.sumOf { it.energy }

    override val pieces: Set<Food.Piece> = Collections.unmodifiableSet(pieceSet)

    @Synchronized
    override fun attemptCollecting(blob: Blob): Set<Blob> = pieceSet
        .any { it.collectedBy.compareAndSet(null, blob) }
        .takeIf { it }
        ?.let { pieceSet.mapNotNull { piece -> piece.collectedBy.get() }.toSet() }
        ?.also { post(UpdatedFood(copy())) }
        .orEmpty()

    private class PieceImpl(override val energy: Energy) : Food.Piece {
        val collectedBy: AtomicReference<Blob?> = AtomicReference(null)

        override fun collectedBy(): Blob? = collectedBy.get()
    }
}
