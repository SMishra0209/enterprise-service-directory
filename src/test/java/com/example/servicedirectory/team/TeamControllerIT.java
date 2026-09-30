package com.example.servicedirectory.team;

import com.example.servicedirectory.TestcontainersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TeamControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTeam_returns201AndBody() throws Exception {
        String body = """
                {"name": "Platform Team", "contactEmail": "platform@example.com"}
                """;

        mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Platform Team"))
                .andExpect(jsonPath("$.contactEmail").value("platform@example.com"));
    }

    @Test
    void createTeam_blankName_returns400WithFieldError() throws Exception {
        String body = """
                {"name": "", "contactEmail": "platform@example.com"}
                """;

        mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"));
    }

    @Test
    void createTeam_duplicateName_returns409() throws Exception {
        String body = """
                {"name": "Duplicate Team", "contactEmail": "dup@example.com"}
                """;

        mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getTeam_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/teams/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void getTeam_found_returnsTeam() throws Exception {
        String body = """
                {"name": "Lookup Team", "contactEmail": "lookup@example.com"}
                """;

        String response = mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/teams/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lookup Team"));
    }

    @Test
    void updateTeam_returns200AndUpdatedBody() throws Exception {
        String createBody = """
                {"name": "Old Name", "contactEmail": "old@example.com"}
                """;
        String response = mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        String updateBody = """
                {"name": "New Name", "contactEmail": "new@example.com"}
                """;

        mockMvc.perform(put("/api/v1/teams/" + id)
                        .contentType("application/json")
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteTeam_returns204() throws Exception {
        String createBody = """
                {"name": "Deletable Team", "contactEmail": "delete@example.com"}
                """;
        String response = mockMvc.perform(post("/api/v1/teams")
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/teams/" + id))
                .andExpect(status().isNoContent());
    }
}