package com.example.servicedirectory.serviceentry;

import com.example.servicedirectory.TestcontainersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ServiceEntryControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createTeam(String name) throws Exception {
        String body = """
                {"name": "%s", "contactEmail": "%s@example.com"}
                """.formatted(name, name.toLowerCase().replace(" ", "."));

        String response = mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private String serviceBody(String name, Long teamId, String status) {
        return """
                {"name": "%s", "description": "test service", "status": "%s",
                 "environment": "PROD", "teamId": %d}
                """.formatted(name, status, teamId);
    }

    @Test
    void createService_returns201AndBody() throws Exception {
        Long teamId = createTeam("Create Svc Team");

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Auth Service", teamId, "ACTIVE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Auth Service"))
                .andExpect(jsonPath("$.teamId").value(teamId))
                .andExpect(jsonPath("$.teamName").value("Create Svc Team"));
    }

    @Test
    void createService_blankName_returns400() throws Exception {
        Long teamId = createTeam("Blank Name Team");

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("", teamId, "ACTIVE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void createService_unknownTeam_returns404() throws Exception {
        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Orphan Service", 999999L, "ACTIVE")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createService_duplicateName_returns409() throws Exception {
        Long teamId = createTeam("Duplicate Svc Team");
        String body = serviceBody("Duplicate Service", teamId, "ACTIVE");

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void getService_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/services/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateService_returns200AndUpdatedBody() throws Exception {
        Long teamId = createTeam("Update Svc Team");
        String response = mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Old Service Name", teamId, "ACTIVE")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/v1/services/" + id)
                        .contentType("application/json")
                        .content(serviceBody("New Service Name", teamId, "DEPRECATED")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Service Name"))
                .andExpect(jsonPath("$.status").value("DEPRECATED"));
    }

    @Test
    void deleteService_returns204() throws Exception {
        Long teamId = createTeam("Delete Svc Team");
        String response = mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Deletable Service", teamId, "ACTIVE")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/services/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void listServices_filterByStatus_returnsOnlyMatching() throws Exception {
        Long teamId = createTeam("Filter Svc Team");

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Filter Active Service", teamId, "ACTIVE")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/services")
                        .contentType("application/json")
                        .content(serviceBody("Filter Retired Service", teamId, "RETIRED")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/services")
                        .param("teamId", teamId.toString())
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("ACTIVE"))))
                .andExpect(jsonPath("$.content.length()").value(1));
    }
}