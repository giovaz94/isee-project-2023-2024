package io.github.evasim.agents.actions

import io.github.evasim.agents.Literals.move_towards
import io.github.evasim.agents.Literals.update_direction
import io.github.evasim.agents.Literals.update_velocity
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction

internal object MoveTowards : AbstractExternalAction(move_towards, 2) {
    override fun action(request: ExternalRequest) {
        val vx = request.arguments[0].asReal()!!.value.toDouble()
        val vy = request.arguments[1].asReal()!!.value.toDouble()
        updateData(Pair(move_towards, Triple(request.sender, vx, vy)))
    }
}

internal object UpdateVelocity : AbstractExternalAction(update_velocity, 2) {
    override fun action(request: ExternalRequest) {
        val vx = request.arguments[0].asReal()!!.value.toDouble()
        val vy = request.arguments[1].asReal()!!.value.toDouble()
        updateData(Pair(update_velocity, Triple(request.sender, vx, vy)))
    }
}

internal object UpdateDirection : AbstractExternalAction(update_direction, 2) {
    override fun action(request: ExternalRequest) {
        val vx = request.arguments[0].asReal()!!.value.toDouble()
        val vy = request.arguments[1].asReal()!!.value.toDouble()
        updateData(Pair(update_direction, Triple(request.sender, vx, vy)))
    }
}
