package io.github.evasim.utils

import io.github.evasim.model.Position2D
import io.github.evasim.model.Vector2D
import io.github.evasim.model.asVector2D
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple

/** Utility functions for logic programming in the BDI Jakta framework. */
object Logic {

    /** Builds a [Struct] from the given [Position2D]. */
    operator fun String.invoke(p: Position2D): Struct = invoke(p.asVector2D())

    /** Builds a [Struct] from the given [Vector2D]. */
    operator fun String.invoke(v: Vector2D): Struct = Struct.of(this, Tuple.of(Real.of(v.x), Real.of(v.y)))

    /** Builds a [Struct] from the given [String]. */
    operator fun String.invoke(s: String): Struct = Struct.of(this, Atom.of(s))

    /** Builds a simple [Struct] with no terms. */
    operator fun String.invoke(): Struct = Struct.of(this)

    /** Builds a [Belief] from the given [Struct]. */
    fun Struct.asBelief(): Belief = Belief.fromPerceptSource(this)

    /** Builds a [Vector2D] out of a [Tuple] with two [Real] values throwing an exception if the cast fails. */
    fun Tuple.castToVector2D(): Vector2D =
        Vector2D(left.castToReal().value.toDouble(), right.castToReal().value.toDouble())

    /** Builds a [Tuple] out of this [Vector2D]. */
    fun Vector2D.castToVector2D(): Tuple = Tuple.of(Real.of(x), Real.of(y))

    /** Cast to an integer this [Term], possibly throwing an exception. */
    fun Term.castToInt(): Int = castToInteger().value.toIntExact()

    /** Cast to a double this [Term], possibly throwing an exception. */
    fun Term.castToDouble(): Double = castToReal().value.toDouble()
}
