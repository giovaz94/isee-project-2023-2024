package io.github.evasim.agents.environment

import io.github.evasim.model.World
import it.unibo.jakta.agents.bdi.AgentID
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.environment.impl.EnvironmentImpl
import it.unibo.jakta.agents.bdi.messages.MessageQueue
import it.unibo.jakta.agents.bdi.perception.Perception

class SimulationEnvironment(world: World): EnvironmentImpl(
    externalActions = emptyMap(),
    perception = TODO(),
) {

    override fun percept(): BeliefBase {
        return TODO()
    }

    override fun copy(
        agentIDs: Map<String, AgentID>,
        externalActions: Map<String, ExternalAction>,
        messageBoxes: Map<AgentID, MessageQueue>,
        perception: Perception,
        data: Map<String, Any>
    ): Environment {
        TODO()
    }
}
