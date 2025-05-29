package io.github.evasim.agents

import io.github.evasim.model.Vector2D
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction

internal object UpdateExternalAction : AbstractExternalAction(name = update, arity = 3) {
    override fun action(request: ExternalRequest) {
        val dx = request.arguments[0].castToReal().value.toDouble()
        val dy = request.arguments[1].castToReal().value.toDouble()
        val speed = request.arguments[2].castToReal().value.toDouble()
        val velocity = Vector2D(dx, dy) * speed
        updateData(update to Triple(request.sender, velocity, request.requestTimestamp))
    }
}
