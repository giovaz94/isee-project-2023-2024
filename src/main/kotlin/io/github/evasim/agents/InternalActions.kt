package io.github.evasim.agents

import io.github.evasim.model.Vector2D
import io.github.evasim.model.zero
import it.unibo.jakta.agents.bdi.actions.InternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractInternalAction
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
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
        val bx = request.arguments[0].castToReal().value.toDouble()
        val by = request.arguments[1].castToReal().value.toDouble()
        val tx = request.arguments[2].castToReal().value.toDouble()
        val ty = request.arguments[3].castToReal().value.toDouble()
        val dirX = request.arguments[4].castToVar()
        val dirY = request.arguments[5].castToVar()

        val blobPosition = Vector2D(bx, by)
        val targetPosition = Vector2D(tx, ty)
        val resultPosition = (targetPosition - blobPosition).normalized() ?: zero

        addResults(
            Substitution.unifier(
                dirX to Real.of(resultPosition.x),
                dirY to Real.of(resultPosition.y),
            ),
        )
    }
}
