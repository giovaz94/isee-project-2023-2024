package io.github.evasim.model

import java.util.Collections
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration

/** A type alias for the energy amount. */
typealias Energy = Double

/** A collectable food item in the simulation. */
interface Food : Entity {

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

    /** Attempts to collect this food item by the given blob, returning true if successful or false otherwise. */
    fun attemptCollecting(blob: Blob): Boolean

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

private data class FoodImpl(
    override val id: Entity.Id,
    override val shape: Shape,
    override val position: Position2D,
    private val numPieces: Int,
) : Food {

    private val pieceSet = (1..numPieces).map { PieceImpl(energy = 1.0) }.toSet()

    override val totalEnergy: Energy = pieceSet.sumOf { it.energy }

    override val pieces: Set<Food.Piece> = Collections.unmodifiableSet(pieceSet)

    override fun attemptCollecting(blob: Blob): Boolean = pieceSet
        .any { it.collectedBy.compareAndSet(null, blob) }

    override fun update(elapsedTime: Duration) {
        // Do nothing
    }

    private class PieceImpl(override val energy: Energy) : Food.Piece {
        var collectedBy: AtomicReference<Blob?> = AtomicReference(null)

        override fun collectedBy(): Blob? = collectedBy.get()
    }
}
