package de.digitalfrontiers.protobuf.web

import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvents
import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvent
import de.digitalfrontiers.protobuf.services.DeploymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class DeploymentController(val deploymentService: DeploymentService) {

    @GetMapping(value = ["/deployments/{deploymentId}"], produces = [PROTOBUF_MEDIA_TYPE_VALUE])
    fun findDeploymentById(@PathVariable deploymentId: Int): ResponseEntity<DeploymentEvent> =
        deploymentService
            .findDeploymentById(deploymentId)
            ?.let {
                ResponseEntity.ok(it)
            } ?: ResponseEntity(HttpStatus.NOT_FOUND)

    @GetMapping(value = ["/deployments"], produces = [PROTOBUF_MEDIA_TYPE_VALUE])
    fun findDeploymentsByTarget(@RequestParam target: String): ResponseEntity<DeploymentEvents> =
        runCatching {
            DeploymentEvent.Target.valueOf(target)
        }.map {
            deploymentService
                .findDeploymentsByTarget(it)
                .let { event ->
                    ResponseEntity.ok(event)
                }
        }.getOrElse {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }

    @PostMapping(value = ["/deployments"], consumes = [PROTOBUF_MEDIA_TYPE_VALUE], produces = [PROTOBUF_MEDIA_TYPE_VALUE])
    fun saveDeployment(@RequestBody deploymentEvent: DeploymentEvent): ResponseEntity<DeploymentEvent> =
        deploymentService
            .saveDeployment(deploymentEvent)
            .let {
                ResponseEntity.ok(it)
            }

    companion object {
        const val PROTOBUF_MEDIA_TYPE_VALUE = "application/x-protobuf"
    }
}
