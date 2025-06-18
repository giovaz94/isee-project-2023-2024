package io.github.evasim.agents

import io.github.evasim.model.contentionRule
import io.github.evasim.model.toPersonality
import io.github.evasim.model.zero
import io.github.evasim.utils.Logic.castToEnergy
import io.github.evasim.utils.Logic.castToVector2D
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.messages.Message
import it.unibo.jakta.agents.bdi.messages.Tell
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal object Update : AbstractExternalAction(name = update, arity = 2) {
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

internal object CheckContention : AbstractExternalAction(check_contention, arity = 4) {
    private const val MAX_CONTESTANT_NUMBER = 2
    override fun action(request: ExternalRequest) {
        val blobList = request.arguments[0].castToList().toList()
        val personality = request.arguments[1].castToAtom()
        val energy = request.arguments[2].castToReal()
        val foodId = request.arguments[3].castToAtom()

        if (blobList.size == MAX_CONTESTANT_NUMBER) {
            val sender = request.sender
            val message = Message(
                sender,
                Tell,
                Struct.Companion.of(contention, personality, energy, foodId),
            )
            blobList
                .map { it.toString().removeSurrounding("'") }
                .filter { it != sender }
                .forEach {
                    sendMessage(it, message)
                }
        }
    }
}

internal object SolveContention : AbstractExternalAction(solve_contention, arity = 6) {
    override fun action(request: ExternalRequest) {
        val foodId = request.arguments[0].castToAtom().value
        val contenderId = request.arguments[1].castToAtom()
            .toString()
            .removeSurrounding("'")
        val solverPersonality = request.arguments[2].castToAtom()
            .toString()
            .removeSurrounding("'")
            .toPersonality()!!
        val contenderPersonality = request.arguments[3].castToAtom()
            .toString()
            .removeSurrounding("'")
            .toPersonality()!!
        val totalEnergy = request.arguments[4].castToReal().value.toDouble().castToEnergy()
        val solverEnergy = request.arguments[5].castToVar()
        val ruleOutput = contentionRule(solverPersonality, contenderPersonality, totalEnergy)
        println(ruleOutput)
        val contenderEnergy = ruleOutput.second
        updateData(remove_food to foodId)
        sendMessage(contenderId, Message(request.sender, Tell, Struct.of(contention_result, Real.of(contenderEnergy))))
        addResults(
            Substitution.unifier(solverEnergy to Real.of(ruleOutput.first)),
        )
    }
}
