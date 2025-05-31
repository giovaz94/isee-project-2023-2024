package io.github.evasim.agents

import io.github.evasim.model.zero
import io.github.evasim.utils.Logic.castToVector2D
import it.unibo.jakta.agents.bdi.actions.InternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractInternalAction
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple
import kotlin.random.Random

internal object Random : AbstractInternalAction(random, arity = 3) {
    override fun action(request: InternalRequest) {
        val randomValue = request.arguments[0].castToVar()
        if (request.arguments[1].isInteger) {
            val lowerBound = request.arguments[1].castToInteger().value.toInt()
            val upperBound = request.arguments[2].castToInteger().value.toInt()
            val generatedValue = Random.nextInt(from = lowerBound, until = upperBound)
            addResults(Substitution.unifier(randomValue to Integer.of(generatedValue)))
        } else if (request.arguments[1].isReal) {
            val lowerBound = request.arguments[1].castToReal().value.toDouble()
            val upperBound = request.arguments[2].castToReal().value.toDouble()
            val generatedValue = Random.nextDouble(from = lowerBound, until = upperBound)
            addResults(Substitution.unifier(randomValue to Real.of(generatedValue)))
        } else {
            error("random/3 is only available on Doubles or Integers")
        }
    }
}

internal object WaypointDirection : AbstractInternalAction(waypoint_direction, arity = 6) {
    override fun action(request: InternalRequest) {
        val blobPosition = request.arguments[0].castToTuple().castToVector2D()
        val targetPosition = request.arguments[1].castToTuple().castToVector2D()
        val newDirection = request.arguments[2].castToVar()
        val resultPosition = (targetPosition - blobPosition).normalized() ?: zero
        addResults(
            Substitution.unifier(
                newDirection to Tuple.of(Real.of(resultPosition.x), Real.of(resultPosition.y)),
            ),
        )
    }
}

internal object InverseDirection: AbstractInternalAction(inverse_direction, arity = 2) {
    override fun action(request: InternalRequest) {
        val oldDirection = request.arguments[0].castToTuple().castToVector2D()
        val newDirection = request.arguments[1].castToVar()

        val inverseVector = oldDirection.invertedWithRandomAngle(-45.0, 45.0)

        addResults(
            Substitution.unifier(
                newDirection to Tuple.of(Real.of(inverseVector.x), Real.of(inverseVector.y)),
            )
        )

    }
}
