package io.github.evasim.utils

import io.github.evasim.model.Position2D
import io.github.evasim.model.Vector2D
import io.github.evasim.model.asVector2D
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

/** Utility functions for logic programming in the BDI Jakta framework. */
object Logic {

//    /**
//     * Execute `this` [ExternalAction] with the given arguments.
//     * This is a shorthand for `execute(<action_literal>(arg, *args))` and can be called from a [BodyScope].
//     */
//    context (body: BodyScope)
//    operator fun <T : ExternalAction> T.invoke(arg: Any, vararg args: Any) = with (body) {
//        execute(this@invoke.signature.name(arg, *args))
//    }

    /** Builds a [Struct] from the given [Position2D]. */
    operator fun String.invoke(p: Position2D): Struct = invoke(p.asVector2D())

    /** Builds a [Struct] from the given [Vector2D]. */
    operator fun String.invoke(v: Vector2D): Struct = Struct.of(this, Real.of(v.x), Real.of(v.y))

    /** Builds a [Struct] from the given [String]. */
    operator fun String.invoke(s: String): Struct = Struct.of(this, Atom.of(s))

    /** Builds a [Belief] from the given [Struct]. */
    fun Struct.asBelief(): Belief = Belief.fromPerceptSource(this)

    /** A term representing the self source annotation in logic programming. */
    val selfSource: Term = "source"("self")
}
