package lab2;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class SendStatusTest {

    // Public repository information
    String realOwner = "ElinorSel"; 
    String realRepo = "DD2480-group13-continuous-integration-server";
    String realSha = "6862bb712f9d6fa1365814dd336e7f488d3978b6";

    /**
     * Contract: The function must return true if the status update "success" is successfully sent to the GitHub API (=HTTP 201).
     * Input: Valid owner, valid repository, valid SHA (commit hash) and state = "success".
     * Output: True
     */
    @Test
    void testSendSuccess() throws URISyntaxException {
        boolean result = SendStatus.sendingStatus(realOwner, realRepo, realSha, "success",
        "http://example.com", "Maven test passed");
        
        assertTrue(result, "The status should be sent successfully (return true)");
    }

    /**
     * Contract: The function must return true if the status update "failure" is successfully sent to the GitHub API (=HTTP 201).
     * Input: Input: Valid owner, valid repository, valid SHA (commit hash) and state = "failure".
     * Output: True (The message was delivered successfully, even though it reports a failure)
     */
    @Test
    void testSendFailure() throws URISyntaxException {
        boolean result = SendStatus.sendingStatus(realOwner, realRepo, realSha, "failure",
        "http://example.com", "Maven test failed");
        
        assertTrue(result, "Should return true even when sending a failure status");    
    }

    /**
     * Contract: The function must return false if the target repository or owner does not exist (=HTTP 404).
     * Input: Invalid owner = "fake-repo-owner", valid repository, valid SHA (commit hash) and state = "failure".
     * Output: False
     */
    @Test
    void testInvalidRepoOwner() throws URISyntaxException {
        boolean result = SendStatus.sendingStatus("fake-repo-owner", realRepo, realSha, "failure",
        "http://example.com", "Maven test should fail");
        
        assertFalse(result, "Should return false because the repo owner does not exist");    
    }

}
