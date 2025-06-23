package io.github.evasim.agents

import io.github.evasim.model.zero
import io.github.evasim.utils.Logic.castToDouble
import io.github.evasim.utils.Logic.castToInt
import io.github.evasim.utils.Logic.castToVector2D
import io.github.evasim.utils.Rnd
import it.unibo.jakta.agents.bdi.actions.InternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractInternalAction
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple

/**
 * `random(-RandomValue, +LowerBound, +UpperBound)` internal action that generates a random value.
 */
internal object Random : AbstractInternalAction(random, arity = 3) {
    override fun action(request: InternalRequest) {
        val randomValue = request.arguments[0].castToVar()
        val lowerBound = request.arguments[1]
        val upperBound = request.arguments[2]
        val generatedResult = when {
            lowerBound.isInteger -> Rnd.nextInt(from = lowerBound.castToInt(), until = upperBound.castToInt())
            lowerBound.isReal -> Rnd.nextDouble(from = lowerBound.castToDouble(), until = upperBound.castToDouble())
            else -> error("random/3 is only available on Integers or Doubles")
        }
        addResults(Substitution.unifier(randomValue to Numeric.of(generatedResult)))
    }
}

/**
 * `waypoint_direction(+BlobPosition, +TargetPosition, -NewDirection)` internal action that calculates
 * the direction that the blob should take to reach the target position.
 */
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

/**
 * `inverse_direction(+OldDirection, -NewDirection)` internal action that calculates the inverse direction with
 * a refracted angle between -45 and +45 degrees.
 */
internal object InverseDirection : AbstractInternalAction(inverse_direction, arity = 2) {
    override fun action(request: InternalRequest) {
        val oldDirection = request.arguments[0].castToTuple().castToVector2D()
        val newDirection = request.arguments[1].castToVar()
        val inverseVector = oldDirection.invertedWithRandomAngle(minDegrees = -45.0, maxDegrees = 45.0)
        addResults(
            Substitution.unifier(
                newDirection to Tuple.of(Real.of(inverseVector.x), Real.of(inverseVector.y)),
            ),
        )
    }
}

/**
 * `end_round()` internal action that stops the agent.
 */
internal object EndRound : AbstractInternalAction(stop_agent, arity = 0) {
    override fun action(request: InternalRequest) = stopAgent()
}
