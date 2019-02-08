package de.digitalfrontiers.protobuf.persistence

import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvent
import javax.persistence.*

@Entity
@Table(name = "deployment_events")
data class DeploymentEventEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Enumerated(EnumType.STRING)
    val target: DeploymentEvent.Target,

    @Column
    val technology: String,

    @Column
    val product: String,

    @Column
    val version: String,

    @Enumerated(EnumType.STRING)
    val status: DeploymentEvent.Status
)
