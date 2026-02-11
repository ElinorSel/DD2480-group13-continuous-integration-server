package lab2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the HistoryHandler class.
 * Tests build persistence, retrieval, and HTML generation.
 */
public class HistoryHandlerTest {

    private HistoryHandler handler;
    private static final String TEST_DIR = "./builds";

    @BeforeEach
    void setUp() {
        handler = new HistoryHandler();
    }

    @AfterEach
    void tearDown() {
        // Delete all files in the history directory after each test
        File dir = new File(TEST_DIR);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Contract: The function must return a valid build ID (timestamp) and create a corresponding text file in the history directory.
     * Input: Valid commit SHA string, valid log string, and status = "Success".
     * Output: True (The returned ID is not null and the file actually exists on the disk).
     */
    @Test
    void testSaveBuildCreatesFile() {
        String id = handler.saveBuild("sha123", "log content", "Success");

        assertNotNull(id, "Build ID should be generated");
        File file = new File(TEST_DIR, id + ".txt");
        assertTrue(file.exists(), "Build file should be created on disk");
    }

    /**
     * Contract: The function must return an HTML string that correctly displays the details of a previously saved build.
     * Input: A valid build ID corresponding to an existing file containing "sha_test_content" and "Failure".
     * Output: True (The HTML string contains the SHA, the Status, and the Log content).
     */
    @Test
    void testGetBuildDetailReturnsCorrectHtml() {
        String sha = "sha_test_content";
        String status = "Failure";
        String log = "Compiler error at line 10";
        String id = handler.saveBuild(sha, log, status);

        String html = handler.getBuildDetailHtml(id);

        assertTrue(html.contains(sha), "HTML page should display the Commit SHA");
        assertTrue(html.contains(status), "HTML page should display the Status");
        assertTrue(html.contains(log), "HTML page should display the Logs");
    }

    /**
     * Contract: The function must return an HTML string listing all saved builds with clickable links to their details.
     * Input: Two previously saved builds with unique IDs.
     * Output: True (The HTML string contains the IDs of both builds and correct href links).
     */
    @Test
    void testGetHistoryListContainsLinks() {
        String id1 = handler.saveBuild("sha1", "log1", "Success");
        String id2 = handler.saveBuild("sha2", "log2", "Success");

        String listHtml = handler.getHistoryListHtml();

        assertTrue(listHtml.contains(id1), "History list should contain Build ID 1");
        assertTrue(listHtml.contains(id2), "History list should contain Build ID 2");
        assertTrue(listHtml.contains("href='/builds/" + id1 + "'"), "Should have a clickable link to build 1");
    }

    /**
     * Contract: The function must return a user-friendly error message if the requested build ID does not exist.
     * Input: A non-existent build ID "non_existent_id_999".
     * Output: True (The HTML string contains the message "Build not found").
     */
    @Test
    void testGetMissingBuildReturnsError() {
        String html = handler.getBuildDetailHtml("non_existent_id_999");

        assertTrue(html.contains("Build not found"), "Should display error message for missing files");
    }
}