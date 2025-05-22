package io.github.evasim.agents.environment

import io.github.evasim.agents.Literals.food
import io.github.evasim.agents.Literals.my_position
import io.github.evasim.agents.Literals.stop_moving
import io.github.evasim.agents.actions.StopMoving
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
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

/**
 * Simulation agent environment.
 */
class SimulationEnvironment(private val world: World) : EnvironmentImpl(
    externalActions = mapOf(stop_moving to StopMoving),
    perception = Perception.empty(),
) {

    override fun percept(agent: Agent): BeliefBase = world.blobs.find { it.id.value == agent.name }?.let { blob ->
        val foodsInSight = world.foods.filter { it in blob.sight }.toSet()
        val foodsBeliefs = foodsInSight.map {
            Belief.fromPerceptSource(
                Struct.of(food, Numeric.of(it.position.x), Numeric.of(it.position.y)),
            )
        }
        BeliefBase.of(
            Belief.fromPerceptSource(
                Struct.of(my_position, Numeric.of(blob.position.x), Numeric.of(blob.position.y)),
            ),
            *foodsBeliefs.toTypedArray(),
        )
    } ?: BeliefBase.empty()

    @Suppress("UNCHECKED_CAST")
    override fun updateData(newData: Map<String, Any>): Environment {
        var newEnv = this
        if ("update_velocity" in newData) {
            val (agentID, vx, vy) = newData["update_velocity"] as Triple<String, Double, Double>
            world.blobs.find { it.id.value == agentID }?.updateVelocity(Vector2D(vx, vy))
            newEnv = SimulationEnvironment(world)
        }
        return newEnv
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(world)
}
