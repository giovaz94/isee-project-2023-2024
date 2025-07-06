package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.model.Energy
import io.github.evasim.model.Food
import io.github.evasim.model.Round
import io.github.evasim.model.Vector2D
import io.github.evasim.model.collidesWith
import io.github.evasim.model.distanceTo
import io.github.evasim.utils.Logic.asBelief
import io.github.evasim.utils.Logic.invoke
import io.github.evasim.utils.OwnName
import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.AgentID
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.environment.impl.EnvironmentImpl
import it.unibo.jakta.agents.bdi.messages.MessageQueue
import it.unibo.jakta.agents.bdi.perception.Perception
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.toTerm
import it.unibo.tuprolog.core.List as TpList

/**
 * The list of blob contending for a piece of food.
 */
typealias Contenders = List<Blob>

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(
    private val round: Round,
    agentIDs: Map<String, AgentID> = emptyMap(),
    externalActions: Map<String, ExternalAction> = mapOf(
        update to Update,
        collect to CollectFood,
        check_contention to CheckContention(),
        solve_contention to SolveContention,
    ),
    messageBoxes: Map<AgentID, MessageQueue> = emptyMap(),
    perception: Perception = Perception.empty(),
    data: Map<String, Any> = mapOf(collectedFood to mutableMapOf<Blob, Pair<Food?, Contenders>>()),
) : EnvironmentImpl(externalActions, agentIDs, messageBoxes, perception, data) {

    private val collectedFoodData: Map<Blob, Pair<Food?, Contenders>>
        @Suppress("UNCHECKED_CAST")
        get() = (data[collectedFood] as? Map<Blob, Pair<Food?, Contenders>>).orEmpty()

    override fun percept(agent: Agent): BeliefBase = round.world.findBlob(agent.name)?.let { blob ->
        BeliefBase.of(
            buildList {
                add(position(blob.position).asBelief())
                addAll(blobBounce(blob))
                addAll(foodsCollidingWith(blob))
                addAll(setOfNotNull(foodsSurrounding(blob), collectedFoodOutcomes(blob), endedRound()))
            },
        )
    } ?: BeliefBase.empty()

    private fun foodsSurrounding(blob: Blob): Belief? = round.world.foods
        .filter { it in blob.sight && it.hasUncollectedPieces() }
        .sortedWith(compareBy<Food> { if (it.hasCollectedPieces()) 0 else 1 }.thenBy { it.distanceTo(blob) })
        .firstOrNull()
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

    private fun collectedFoodOutcomes(blob: Blob): Belief? = collectedFoodData[blob]?.let { (food, contenders) ->
        when {
            food == null || contenders.isEmpty() -> not_collected_food().asBelief()
            else -> Struct.of(
                collected_food,
                Atom.of(food.id.value),
                TpList.from(contenders.map { it.id.value.toTerm() }.asSequence()),
                Real.of(food.totalEnergy),
            ).asBelief()
        }
    }

    private fun endedRound(): Belief? = if (round.isEnded()) ended_round().asBelief() else null

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        val collectedFoods = collectedFoodData.toMutableMap()
        if (update in newData) {
            val (agentID, velocity) = newData[update] as Pair<String, Vector2D>
            round.world.findBlob(agentID)?.let { blob ->
                blob.updateVelocity(velocity)
                blob.update()
            }
        }
        if (collect in newData) {
            val (agentID, foodID) = newData[collect] as Pair<String, String>
            round.world.findBlob(agentID)?.let { blob ->
                collectedFoods[blob] = round.world.findFood(foodID)
                    ?.let { food -> food to food.attemptCollecting(blob).toList() }
                    ?: (null to emptyList())
            }
        }
        if (remove_food in newData) {
            val foodId = newData[remove_food] as String
            round.world.findFood(foodId)?.let { round.world.removeFood(it) }
        }
        if (update_energy in newData) {
            val energyMap = newData[update_energy] as Map<String, Energy>
            energyMap.forEach { (agentId, energy) -> round.world.findBlob(agentId)?.updateHealth(energy) }
        }
        return copy(data = data + (collectedFood to collectedFoods))
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(round, agentIDs, externalActions, messageBoxes, perception, data)

    private companion object {
        /** The [SimulationEnvironment.data] entry key for the collected food. */
        private val collectedFood by OwnName
    }
}
