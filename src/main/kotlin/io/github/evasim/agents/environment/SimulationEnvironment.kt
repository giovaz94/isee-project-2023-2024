package io.github.evasim.agents.environment

import io.github.evasim.agents.Literals.food
import io.github.evasim.agents.Literals.move_towards
import io.github.evasim.agents.Literals.my_position
import io.github.evasim.agents.Literals.reached_food
import io.github.evasim.agents.Literals.update_velocity
import io.github.evasim.agents.actions.MoveTowards
import io.github.evasim.agents.actions.UpdateVelocity
import io.github.evasim.model.Blob
import io.github.evasim.model.Position2D
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
import io.github.evasim.model.asVector2D
import io.github.evasim.model.distanceTo
import io.github.evasim.model.zero
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

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(private val world: World) : EnvironmentImpl(
    externalActions = mapOf(update_velocity to UpdateVelocity, move_towards to MoveTowards),
    perception = Perception.empty(),
) {

    override fun percept(agent: Agent): BeliefBase = world.blobs.find { it.id.value == agent.name }?.let { blob ->
        BeliefBase.of(
            my_position(blob.position).asBelief(),
            *setOfNotNull(surroundingFreeFoods(blob)).toTypedArray(),
            *collidingFoods(blob).toTypedArray(),
        )
    } ?: BeliefBase.empty()

    private fun surroundingFreeFoods(blob: Blob): Belief? = world.foods
        .filter { it in blob.sight }
        .filter { it.hasUncollectedPieces() }
        .minByOrNull { blob.distanceTo(it) }
        ?.let { food(it.position).asBelief() }

    private fun collidingFoods(blob: Blob): Set<Belief> = world.foods
        .filter { blob collidingWith it }
        .map { reached_food(it.position).asBelief() }
        .toSet()

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        if (update_velocity in newData) {
            val (agentID, vx, vy) = newData[update_velocity] as Triple<String, Double, Double>
            world.blobs.find { it.id.value == agentID }?.updateVelocity(Vector2D(vx, vy))
        }
        if (move_towards in newData) {
            val (agentID, tx, ty) = newData[move_towards] as Triple<String, Double, Double>
            world.blobs.find { it.id.value == agentID }?.let { blob ->
                val target = Position2D(tx, ty)
                val direction = (target - blob.position).asVector2D().normalized() ?: zero
                blob.updateVelocity(direction * blob.velocity.magnitude())
            }
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
