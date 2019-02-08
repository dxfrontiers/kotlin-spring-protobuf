package de.digitalfrontiers.protobuf.services

import de.digitalfrontiers.protobuf.convert.DeploymentEventConverters.toProto
import de.digitalfrontiers.protobuf.convert.DeploymentEventConverters.toEntity
import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvents
import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvent
import de.digitalfrontiers.protobuf.persistence.DeploymentEventRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DeploymentService(val deploymentEventRepository: DeploymentEventRepository) {

    fun findDeploymentById(deploymentId: Int): DeploymentEvent? {
        return deploymentEventRepository
            .findByIdOrNull(deploymentId)
            ?.toProto()
    }

    fun findDeploymentsByTarget(target: DeploymentEvent.Target): DeploymentEvents {
        return deploymentEventRepository
            .findDeploymentEventEntityByTarget(target)
            .fold(DeploymentEvents.newBuilder()) { builder, event ->
                builder.addDeploymentEvent(event.toProto())
            }.build()
    }

    fun saveDeployment(deploymentEvent: DeploymentEvent): DeploymentEvent {
        return deploymentEvent
            .toEntity()
            .let {
                deploymentEventRepository.save(it)
            }.toProto()
    }
}
