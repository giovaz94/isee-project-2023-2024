package io.github.evasim.agents.environment

import io.github.evasim.agents.Literals.my_position
import io.github.evasim.model.World
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
    externalActions = emptyMap(),
    perception = Perception.empty(),
) {

    override fun percept(): BeliefBase {
        println("I'm perceiving the environment")
        println(data)
        return BeliefBase.of(
            Belief.fromPerceptSource(
                Struct.of(my_position, Numeric.of(10), Numeric.of(20))
            )
        )
    }

    override fun updateData(newData: Map<String, Any>): Environment {
        println("Updating data in SimulationEnvironment with new data: $newData")
        return super.updateData(newData)
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>,
    ): Environment = SimulationEnvironment(world)
}
