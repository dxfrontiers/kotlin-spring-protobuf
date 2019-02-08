package de.digitalfrontiers.protobuf.persistence

import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto
import org.springframework.data.repository.CrudRepository

interface DeploymentEventRepository : CrudRepository<DeploymentEventEntity, Int> {
    fun findDeploymentEventEntityByTarget(target: DeploymentEventProto.DeploymentEvent.Target): List<DeploymentEventEntity>
}
