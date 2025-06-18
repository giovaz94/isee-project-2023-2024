package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.model.Food
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
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
import it.unibo.jakta.agents.fsm.time.SimulatedTime
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.toTerm
import kotlin.time.Duration.Companion.milliseconds
import it.unibo.tuprolog.core.List as TpList

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(
    private val world: World,
    agentIDs: Map<String, AgentID> = emptyMap(),
    externalActions: Map<String, ExternalAction> = mapOf(
        update to Update,
        collect to CollectFood,
        check_contention to CheckContention,
        solve_contention to SolveContention,
    ),
    messageBoxes: Map<AgentID, MessageQueue> = emptyMap(),
    perception: Perception = Perception.empty(),
    data: Map<String, Any> = mapOf("collectedFood" to mutableMapOf<Blob, Pair<Food, Boolean>>()),
) : EnvironmentImpl(externalActions, agentIDs, messageBoxes, perception, data) {

    override fun percept(agent: Agent): BeliefBase = world.findBlob(agent.name)?.let { blob ->
        BeliefBase.of(
            position(blob.position).asBelief(),
            *setOfNotNull(foodsSurrounding(blob), collectedFood(blob)).toTypedArray(),
            *foodsCollidingWith(blob).toTypedArray(),
            *blobBounce(blob).toTypedArray(),
        )
    } ?: BeliefBase.empty()

    private fun uncollectedFood(): Sequence<Food> = world.foods
        .filter { it.hasUncollectedPieces() }

    private fun foodsSurrounding(blob: Blob): Belief? = uncollectedFood()
        .filter { it in blob.sight }
        .minByOrNull { blob.distanceTo(it) }
        ?.let { food(it.position).asBelief() }

    private fun foodsCollidingWith(blob: Blob): Set<Belief> = uncollectedFood()
        .filter { blob collidingWith it }
        .map { reached_food(it.id.value).asBelief() }
        .toSet()

    private fun blobBounce(blob: Blob): Set<Belief> = blob.sight.visibilityArea
        .takeIf { it collidesWith world.shape }
        ?.let { setOf(bounce(blob.direction).asBelief()) }
        .orEmpty()

    @Suppress("UNCHECKED_CAST")
    private fun collectedFood(blob: Blob): Belief? = (data["collectedFood"] as? Map<Blob, Pair<Food, List<Blob>>>)
        ?.let {
            it[blob]?.let { (food, blobList) ->
                val terms = blobList.map { it.id.value.toTerm() }.toSet()
                val energy = food.totalEnergy
                Struct.of(
                    collected_food,
                    Atom.of(food.id.value),
                    TpList.from(terms.asSequence()),
                    Real.of(energy),
                ).asBelief()
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        val collectedFoods = data["collectedFood"] as? MutableMap<Blob, Pair<Food, List<Blob>>> ?: mutableMapOf()

        if (update in newData) {
            val (agentID, velocity, elapsedTime) = newData[update] as Triple<String, Vector2D, Time>
            world.findBlob(agentID)?.let { blob ->
                blob.updateVelocity(velocity)
                blob.update((elapsedTime as SimulatedTime).value.milliseconds)
            }
        }

        if (collect in newData) {
            val (agentID, foodID) = newData[collect] as Pair<String, String>
            world.findBlob(agentID)?.let { blob ->
                world.findFood(foodID)?.let { food ->
                    collectedFoods[blob] = food to (food.attemptCollecting(blob) ?: emptyList())
                }
            }
        }

        if (remove_food in newData) {
            val foodId = newData[remove_food] as String
            world.findFood(foodId).let { world.removeFood(it!!) }
        }

        return copy(data = data + ("collectedFood" to collectedFoods))
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(world, agentIDs, externalActions, messageBoxes, perception, data)
}
