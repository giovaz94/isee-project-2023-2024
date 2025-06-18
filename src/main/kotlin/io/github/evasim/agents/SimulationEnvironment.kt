package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.model.Food
import io.github.evasim.model.Round
import io.github.evasim.model.Vector2D
import io.github.evasim.model.collidesWith
import io.github.evasim.model.distanceTo
import io.github.evasim.utils.Logic.asBelief
import io.github.evasim.utils.Logic.invoke
import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.AgentID
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.environment.impl.EnvironmentImpl
import it.unibo.jakta.agents.bdi.messages.MessageQueue
import it.unibo.jakta.agents.bdi.perception.Perception
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.time.Duration.Companion.milliseconds

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(
    private val round: Round,
    agentIDs: Map<String, AgentID> = emptyMap(),
    externalActions: Map<String, ExternalAction> = mapOf(update to Update, collect to CollectFood),
    messageBoxes: Map<AgentID, MessageQueue> = emptyMap(),
    perception: Perception = Perception.empty(),
    data: Map<String, Any> = mapOf("collectedFood" to mutableMapOf<Blob, Pair<Food, Boolean>>()),
) : EnvironmentImpl(externalActions, agentIDs, messageBoxes, perception, data) {

    override fun percept(agent: Agent): BeliefBase = round.world.findBlob(agent.name)?.let { blob ->
        BeliefBase.of(
            position(blob.position).asBelief(),
            *setOfNotNull(foodsSurrounding(blob), collectedFood(blob), endedRound()).toTypedArray(),
            *foodsCollidingWith(blob).toTypedArray(),
            *blobBounce(blob).toTypedArray(),
        )
    } ?: BeliefBase.empty()

    private fun foodsSurrounding(blob: Blob): Belief? = round.world.foods
        .filter { it in blob.sight }
        .filter { it.hasUncollectedPieces() }
        .minByOrNull { blob.distanceTo(it) }
        ?.let { food(it.position).asBelief() }

    private fun foodsCollidingWith(blob: Blob): Set<Belief> = round.world.foods
        .filter { it.hasUncollectedPieces() }
        .filter { blob collidingWith it }
        .map { reached_food(it.id.value).asBelief() }
        .toSet()

    private fun blobBounce(blob: Blob): Set<Belief> = blob.sight.visibilityArea
        .takeIf { it collidesWith round.world.shape }
        ?.let { setOf(bounce(blob.direction).asBelief()) }
        .orEmpty()

    @Suppress("UNCHECKED_CAST")
    private fun collectedFood(blob: Blob): Belief? = (data["collectedFood"] as? Map<Blob, Pair<Food, Boolean>>)?.let {
        it[blob]?.let { (food, response) ->
            Struct.of(collected_food, Atom.of(food.id.value), Atom.of(response.toString())).asBelief()
        }
    }

    private fun endedRound(): Belief? = if (round.isEnded()) ended_round().asBelief() else null

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        val collectedFoods = data["collectedFood"] as? MutableMap<Blob, Pair<Food, Boolean>> ?: mutableMapOf()
        if (update in newData) {
            val (agentID, velocity, elapsedTime) = newData["update"] as Triple<String, Vector2D, Time>
            round.world.findBlob(agentID)?.let { blob ->
                blob.updateVelocity(velocity)
                blob.update(50.milliseconds) // TODO!!!
            }
        }
        if (collect in newData) {
            val (agentID, foodID) = newData[collect] as Pair<String, String>
            round.world.findBlob(agentID)?.let { blob ->
                round.world.findFood(foodID)?.let { food ->
                    collectedFoods[blob] = food to food.attemptCollecting(blob)
                }
            }
        }
        return copy(data = data)
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(round, agentIDs, externalActions, messageBoxes, perception, data)
}
