package io.github.evasim.agents

import io.github.evasim.model.zero
import io.github.evasim.utils.Logic.castToVector2D
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.messages.Achieve
import it.unibo.jakta.agents.bdi.messages.Message
import it.unibo.tuprolog.core.Struct

internal object Update : AbstractExternalAction(name = update, arity = 3) {
    override fun action(request: ExternalRequest) {
        val direction = request.arguments[0].castToTuple().castToVector2D().normalized() ?: zero
        val speed = request.arguments[1].castToReal().value.toDouble()
        val velocity = direction * speed
        updateData(update to Triple(request.sender, velocity, request.requestTimestamp))
    }
}

internal object CollectFood : AbstractExternalAction(name = collect, arity = 1) {
    override fun action(request: ExternalRequest) {
        val foodId = request.arguments[0].castToAtom().value
        updateData(collect to Pair(request.sender, foodId))
    }
}

internal object CheckContention : AbstractExternalAction(check_contention, arity = 1) {
    private const val CONTESTANT_NUMBER = 2
    override fun action(request: ExternalRequest) {
        val blobList = request.arguments[0].castToList().toList()
        if (blobList.size == CONTESTANT_NUMBER) {
            val sender = request.sender
            blobList
                .map { it.toString().removeSurrounding("'") }
                .filter { it != sender }
                .forEach {
                    sendMessage(it, Message(sender, Achieve, Struct.Companion.of(contention)))
                }
        }
    }
}
