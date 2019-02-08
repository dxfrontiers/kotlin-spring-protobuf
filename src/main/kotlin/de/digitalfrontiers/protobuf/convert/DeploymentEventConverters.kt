package de.digitalfrontiers.protobuf.convert

import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvent
import de.digitalfrontiers.protobuf.persistence.DeploymentEventEntity

object DeploymentEventConverters {
    fun DeploymentEvent.toEntity(): DeploymentEventEntity {
        return DeploymentEventEntity(
            id = id,
            target = target,
            version = version,
            technology = technology,
            product = product,
            status = status
        )
    }

    fun DeploymentEventEntity.toProto(): DeploymentEvent {
        return DeploymentEvent.newBuilder()
            .setId(id)
            .setTarget(target)
            .setVersion(version)
            .setTechnology(technology)
            .setProduct(product)
            .setStatus(status)
            .build()
    }
}
