package io.github.evasim.model

import java.util.Collections
import java.util.concurrent.atomic.AtomicReference

typealias Energy = Int

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
        /** Creates a new food with the given id, shape, position, and number of pieces. */
        fun of(id: EntityId, shape: Shape, position: Position2D, pieces: Int): Food =
            FoodImpl(id, shape, position, pieces)
    }
}

private class FoodImpl(
    override val id: EntityId,
    override val shape: Shape,
    override val position: Position2D,
    numPieces: Int,
) : Food {

    private val pieceSet = (1..numPieces).map { PieceImpl(energy = 1) }.toSet()

    override val totalEnergy: Energy = pieceSet.sumOf { it.energy }

    override val pieces: Set<Food.Piece> = Collections.unmodifiableSet(pieceSet)

    override fun attemptCollecting(blob: Blob): Boolean = pieceSet
        .any { it.collectedBy.compareAndSet(null, blob) }

    private class PieceImpl(override val energy: Energy) : Food.Piece {
        var collectedBy: AtomicReference<Blob?> = AtomicReference(null)

        override fun collectedBy(): Blob? = collectedBy.get()
    }
}
