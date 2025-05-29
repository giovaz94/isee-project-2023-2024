package io.github.evasim.agents

import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(private val world: World) : EnvironmentImpl(
    externalActions = mapOf(update to UpdateExternalAction),
    perception = Perception.empty(),
) {

    override fun percept(agent: Agent): BeliefBase = (world[Entity.Id(agent.name)] as? Blob)?.let { blob ->
        BeliefBase.of(
            position(blob.position).asBelief(),
            *setOfNotNull(foodsSurrounding(blob)).toTypedArray(),
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
        .map { reached_food(it.position).asBelief() }
        .toSet()

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
//        if (update_velocity in newData) {
//            val (agentID, vx, vy) = newData[update_velocity] as Triple<String, Double, Double>
//            world.blobs.find { it.id.value == agentID }?.updateVelocity(Vector2D(vx, vy))
//        }
//        if (move_towards in newData) {
//            val (agentID, tx, ty) = newData[move_towards] as Triple<String, Double, Double>
//            world.blobs.find { it.id.value == agentID }?.let { blob ->
//                val target = Position2D(tx, ty)
//                val direction = (target - blob.position).asVector2D().normalized() ?: zero
//                blob.updateVelocity(direction * blob.velocity.magnitude())
//            }
//        }
//        if (collect_food in newData) {
//            val (agentID, foodID) = newData[collect_food] as Pair<String, String>
//            world.blobs.find { it.id.value == agentID }?.let { blob ->
//                world.foods.find { it.id.value == foodID }?.let { food ->
//                    food.attemptCollecting(blob)
//                }
//            }
//        }
        if (update in newData) {
            val (agentID, velocity, elapsedTime) = newData["update"] as Triple<String, Vector2D, Time>
            val blobId = Entity.Id(agentID)
            (world[blobId] as? Blob)?.updateVelocity(velocity)
            world.update(Entity.Id(agentID), (elapsedTime as SimulatedTime).value.milliseconds)
        }
        return SimulationEnvironment(world)
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(world)
}
