package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
import io.github.evasim.model.Food
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
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
import it.unibo.jakta.agents.fsm.time.SimulatedTime
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.time.Duration.Companion.milliseconds

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(
    private val world: World,
    agentIDs: Map<String, AgentID> = emptyMap(),
    externalActions: Map<String, ExternalAction> = mapOf(update to Update, collect to CollectFood),
    messageBoxes: Map<AgentID, MessageQueue> = emptyMap(),
    perception: Perception = Perception.empty(),
    data: Map<String, Any> = mapOf("collectedFood" to emptyMap<Blob, Pair<Food, Boolean>>()),
) : EnvironmentImpl(externalActions, agentIDs, messageBoxes, perception, data) {

    override fun percept(agent: Agent): BeliefBase = (world[Entity.Id(agent.name)] as? Blob)?.let { blob ->
        BeliefBase.of(
            position(blob.position).asBelief(),
            *setOfNotNull(foodsSurrounding(blob), collectedFood(blob)).toTypedArray(),
            *foodsCollidingWith(blob).toTypedArray(),
        )
    } ?: BeliefBase.empty()

    private fun foodsSurrounding(blob: Blob): Belief? = world.foods
        .filter { it in blob.sight }
        .filter { it.hasUncollectedPieces() }
        .minByOrNull { blob.distanceTo(it) }
        ?.let { food(it.position).asBelief() }

    private fun foodsCollidingWith(blob: Blob): Set<Belief> = world.foods
        .filter { blob collidingWith it }
        .map { reached_food(it.id.value).asBelief() }
        .toSet()

    @Suppress("UNCHECKED_CAST")
    private fun collectedFood(blob: Blob): Belief? = (data["collectedFood"] as? Map<Blob, Pair<Food, Boolean>>)?.let {
        it[blob]?.let {
            Struct.of(collected_food, Atom.of(it.first.id.value), Atom.of(it.second.toString())).asBelief()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        val newCollectedFoods = mutableMapOf<Blob, Pair<Food, Boolean>>()
        if (update in newData) {
            val (agentID, velocity, elapsedTime) = newData["update"] as Triple<String, Vector2D, Time>
            val blobId = Entity.Id(agentID)
            (world[blobId] as? Blob)?.updateVelocity(velocity)
            world.update(Entity.Id(agentID), (elapsedTime as SimulatedTime).value.milliseconds)
        }
        if (collect in newData) {
            val (agentID, foodID) = newData[collect] as Pair<String, String>
            (world[Entity.Id(agentID)] as? Blob)?.let { blob ->
                (world[Entity.Id(foodID)] as? Food)?.let { food ->
                    food.attemptCollecting(blob).also {
                        newCollectedFoods[blob] = Pair(food, it)
                    }
                }
            }
        }
        val oldData = data["collectedFood"] as? Map<Blob, Pair<Food, Boolean>> ?: emptyMap()
        val newData = oldData + newCollectedFoods
        return copy(data = mapOf("collectedFood" to newData))
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(world, agentIDs, externalActions, messageBoxes, perception, data)
}
