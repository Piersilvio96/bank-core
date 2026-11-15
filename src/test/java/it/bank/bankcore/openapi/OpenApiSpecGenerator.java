package it.bank.bankcore.openapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Generates the OpenAPI spec (JSON + YAML) from the running Spring context.
 * Executed by the Gradle task {@code generateOpenApi}, which is part of {@code build}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiSpecGenerator {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void generateOpenApiSpec() throws Exception {
        Path outputDir = Path.of(System.getProperty("openapi.outputDir", "build/openapi"));
        Files.createDirectories(outputDir);

        write(outputDir.resolve("openapi.json"), "/v3/api-docs");
        write(outputDir.resolve("openapi.yaml"), "/v3/api-docs.yaml");
    }

    private void write(Path target, String endpoint) throws Exception {
        String body = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Files.writeString(target, body, StandardCharsets.UTF_8);
    }
}
