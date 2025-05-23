package io.github.evasim.agents.environment

import io.github.evasim.agents.Literals.food
import io.github.evasim.agents.Literals.move_towards
import io.github.evasim.agents.Literals.my_position
import io.github.evasim.agents.Literals.reached_food
import io.github.evasim.agents.Literals.update_velocity
import io.github.evasim.agents.actions.MoveTowards
import io.github.evasim.agents.actions.UpdateVelocity
import io.github.evasim.model.Position2D
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
import io.github.evasim.model.asVector2D
import io.github.evasim.model.zero
import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.AgentID
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.environment.impl.EnvironmentImpl
import it.unibo.jakta.agents.bdi.messages.MessageQueue
import it.unibo.jakta.agents.bdi.perception.Perception
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import kotlin.let

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(private val world: World) : EnvironmentImpl(
    externalActions = mapOf(update_velocity to UpdateVelocity, move_towards to MoveTowards),
    perception = Perception.empty(),
) {

    override fun percept(agent: Agent): BeliefBase = world.blobs.find { it.id.value == agent.name }?.let { blob ->
        val foodsBeliefs = world.foods.filter { it in blob.sight }.map {
            Belief.fromPerceptSource(
                Struct.of(food, Numeric.of(it.position.x), Numeric.of(it.position.y)),
            )
        }
        val collidingBeliefs = world.foods.filter { blob collidingWith it }.map {
            Belief.fromPerceptSource(
                Struct.of(reached_food, Numeric.of(it.position.x), Numeric.of(it.position.y)),
            )
        }
        BeliefBase.of(
            Belief.fromPerceptSource(
                Struct.of(my_position, Numeric.of(blob.position.x), Numeric.of(blob.position.y)),
            ),
            *foodsBeliefs.toSet().toTypedArray(),
            *collidingBeliefs.toSet().toTypedArray(),
        )
    } ?: BeliefBase.empty()

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
