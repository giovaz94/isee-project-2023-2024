package io.github.evasim.agents

import io.github.evasim.model.castToPersonality
import io.github.evasim.model.contentionRule
import io.github.evasim.model.zero
import io.github.evasim.utils.Logic.castToVector2D
import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.messages.Message
import it.unibo.jakta.agents.bdi.messages.Tell
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

/**
 * An `update(+Direction, +NewSpeed)` external action that updates the blob's position and velocity.
 * Direction is a [it.unibo.tuprolog.core.Tuple] representing a 2D vector, and NewSpeed is a [Real] value.
 */
internal object Update : AbstractExternalAction(name = update, arity = 2) {
    override fun action(request: ExternalRequest) {
        val direction = request.arguments[0].castToTuple().castToVector2D().normalized() ?: zero
        val speed = request.arguments[1].castToReal().value.toDouble()
        val velocity = direction * speed
        updateData(update to Pair(request.sender, velocity))
    }
}

/**
 * `collect_food(+FoodId)` external action where the running agent attempts to collect the food identified by `FoodId`.
 * `FoodId` is an [it.unibo.tuprolog.core.Atom] representing the ID of the food to be collected.
 */
internal object CollectFood : AbstractExternalAction(name = collect, arity = 1) {
    override fun action(request: ExternalRequest) {
        val foodId = request.arguments[0].castToAtom().value
        updateData(collect to Pair(request.sender, foodId))
    }
}

/**
 * `check_contention(+BlobList, +Personality, +Energy, +FoodId)` external action that check if a given Food is
 *  being contented between two agent.
 *  If the contention is verified the Blob that calls the action starts the contention resolving protocol.
 *
 *  `BlobList` is a [it.unibo.tuprolog.core.List] representing the IDs of the agents that are contending the food.
 *  `Personality` is an [it.unibo.tuprolog.core.Atom] representing the personality of the Blob that's currently checking
 *  the contention.
 *  `Energy` is an [it.unibo.tuprolog.core.Real] representing the total energy of the food.
 *  `FoodId` is an [it.unibo.tuprolog.core.Atom] representing the ID of the contended food .
 */
internal class CheckContention(
    private val maxContenders: Int = 2,
) : AbstractExternalAction(check_contention, arity = 4) {
    override fun action(request: ExternalRequest) {
        val contendersId = request.arguments[0].castToList().toList()
        val personality = request.arguments[1].castToAtom()
        val energy = request.arguments[2].castToReal()
        val foodId = request.arguments[3].castToAtom()
        if (contendersId.size == maxContenders) {
            val sender = request.sender
            val message = Message(sender, Tell, Struct.of(contention, personality, energy, foodId))
            contendersId
                .map { it.toString().removeSurrounding("'") }
                .filter { it != sender }
                .forEach { sendMessage(it, message) }
        }
    }
}

/**
 * `solve_contention(+FoodId, +ContenderId, +SolverPersonality, +ContenderPersonality, +TotalEnergy, -SolverEnergy)`
 * external action that resolves contention for food identified by `FoodId` between the agent executing it and the
 * contender identified by `ContenderId`, using the personalities of both agents and the total energy available.
 *
 * `FoodId` is an [it.unibo.tuprolog.core.Atom] representing the ID of the contended food .
 * `ContenderID` an [it.unibo.tuprolog.core.Atom] representing the ID of the contended agent
 * `SolverPersonality` an [it.unibo.tuprolog.core.Atom] representing the personality of the solver
 * `ContenderPersonality` an [it.unibo.tuprolog.core.Atom] representing the personality of the contender
 * `TotalEnergy` is an [it.unibo.tuprolog.core.Real] representing the total energy of the food
 * `SolverEnergy` is an [it.unibo.tuprolog.core.Var] representing the output variable of resulting solver energy.
 */
internal object SolveContention : AbstractExternalAction(solve_contention, arity = 6) {
    override fun action(request: ExternalRequest) {
        val foodId = request.arguments[0].castToAtom().value
        val contenderId = request.arguments[1].castToAtom().value
        val solverPersonality = request.arguments[2].castToAtom().value.castToPersonality()
        val contenderPersonality = request.arguments[3].castToAtom().value.castToPersonality()
        val totalEnergy = request.arguments[4].castToReal().value.toDouble()
        val solverEnergy = request.arguments[5].castToVar()
        val ruleOutput = contentionRule(solverPersonality, contenderPersonality, totalEnergy)
        val contenderEnergy = ruleOutput.second
        updateData(
            remove_food to foodId,
            update_energy to mapOf(contenderId to ruleOutput.second, request.sender to ruleOutput.first),
        )
        sendMessage(
            contenderId,
            Message(request.sender, Tell, Struct.of(contention_result, Real.of(contenderEnergy))),
        )
        addResults(Substitution.unifier(solverEnergy to Real.of(ruleOutput.first)))
    }
}
