package io.github.evasim.agents

import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction

internal object MoveTowards : AbstractExternalAction(move_towards, 2) {
    override fun action(request: ExternalRequest) {
        val vx = request.arguments[0].castToReal().value.toDouble()
        val vy = request.arguments[1].castToReal().value.toDouble()
        updateData(move_towards to Triple(request.sender, vx, vy))
    }
}

internal object UpdateVelocity : AbstractExternalAction(update_velocity, 2) {
    override fun action(request: ExternalRequest) {
        val vx = request.arguments[0].castToReal().value.toDouble()
        val vy = request.arguments[1].castToReal().value.toDouble()
        updateData(update_velocity to Triple(request.sender, vx, vy))
    }
}

internal object UpdateDirection : AbstractExternalAction(update_direction, 1) {
    override fun action(request: ExternalRequest) {
        val degrees = request.arguments[0].castToReal().value.toDouble()
        updateData(update_direction to Pair(request.sender, degrees))
    }
}

internal object CollectFood : AbstractExternalAction(collect_food, 1) {
    override fun action(request: ExternalRequest) {
        val foodId = request.arguments[0].castToAtom().value
        updateData(collect_food to Pair(request.sender, foodId))
    }
}
