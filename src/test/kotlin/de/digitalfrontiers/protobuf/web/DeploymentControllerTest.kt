package de.digitalfrontiers.protobuf.web

import com.nhaarman.mockito_kotlin.whenever
import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvents
import de.digitalfrontiers.protobuf.deployment.model.DeploymentEventProto.DeploymentEvent
import de.digitalfrontiers.protobuf.services.DeploymentService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [DeploymentController::class])
class DeploymentControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var deploymentService: DeploymentService

    @Test
    fun `Assert response can be parsed as protobuf deployment event`() {
        val event = createDeploymentEvent(42)
        whenever(deploymentService.findDeploymentById(anyInt())).thenReturn(event)

        val response = mockMvc
            .perform(get("/api/deployments/42"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsByteArray

        val parsedEvent = DeploymentEvent.parseFrom(response)

        assertThat(event).isEqualTo(parsedEvent)
    }

    @Test
    fun `Assert not found when deployment event is not known`() {
        whenever(deploymentService.findDeploymentById(anyInt())).thenReturn(null)

        mockMvc
            .perform(get("/api/deployments/42"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Assert valid deployment event can be saved`() {
        val event = createDeploymentEvent(42)
        whenever(deploymentService.saveDeployment(event)).thenReturn(event)

        val response = mockMvc
            .perform(post("/api/deployments")
                .contentType("application/x-protobuf")
                .content(event.toByteArray()))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsByteArray

        val parsedEvent = DeploymentEvent.parseFrom(response)

        assertThat(event).isEqualTo(parsedEvent)
    }

    @Test
    fun `Assert response can be parsed as repeated deployment events`() {
        val events = DeploymentEvents.newBuilder().addAllDeploymentEvent(Arrays.asList(
            createDeploymentEvent(42),
            createDeploymentEvent(43)
        )).build()
        whenever(deploymentService.findDeploymentsByTarget(DeploymentEvent.Target.ACCEPTANCE)).thenReturn(events)

        val response = mockMvc
            .perform(get("/api/deployments?target=ACCEPTANCE"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsByteArray

        val parsedEvents = DeploymentEvents.parseFrom(response)

        assertThat(events.deploymentEventList).hasSameElementsAs(parsedEvents.deploymentEventList)
    }

    @Test
    fun `Assert bad request when unknown target is request`() {
        mockMvc
            .perform(get("/api/deployments?target=UNKNOWN"))
            .andExpect(status().isBadRequest)
    }

    private fun createDeploymentEvent(id: Int): DeploymentEvent =
        DeploymentEvent
            .newBuilder()
            .setId(id)
            .setProduct("Oracle11")
            .setTechnology("Database")
            .setTarget(DeploymentEvent.Target.ACCEPTANCE)
            .setVersion("0.0.1")
            .setStatus(DeploymentEvent.Status.SUCCESSFUL)
            .build()
}