package lab2;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContinousIntegrationServerTest {

    String realOwner = "FakeOwner";
    String realRepo = "FakeRepo";
    String realSha = "11111111111111111111111111111111111111111";
    String payloadString;

    @Test
    /**
     * Contract: The function must parse the JSON string and extract the owner, repo, sha from the payload.
     * Input: The test payload JSON string from the payload.json file.
     * Output: The owner, repo, sha.
     */
    void testParseJSON() throws Exception {
        String jsonPath = "src/test/java/lab2/payload.json";
        payloadString = new String(Files.readAllBytes(Paths.get(jsonPath)), StandardCharsets.UTF_8);
        
        // Create an instance and parse the JSON
        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        server.parseJSON(payloadString);
        
        // Verify the instance variables were set correctly
        assertEquals(realOwner, server.owner);
        assertEquals(realRepo, server.repo);
        assertEquals(realSha, server.sha);
    }

    @Test
    /**
     * Contract: The function should throw an exception if the payload is empty.
     * Input: Empty string.
     * Output: Exception thrown.
     */
    void testParseJSONEmptyPayload ()throws Exception{
        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        payloadString = "";
        Exception exception = assertThrows(Exception.class, () -> server.parseJSON(payloadString));
        assertEquals("No payload received", exception.getMessage());
    }
}
