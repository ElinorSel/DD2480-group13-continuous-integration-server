package lab2;

import org.junit.jupiter.api.Test; // Or org.junit.Test if using JUnit 4
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectTesterTest {
    /**
     * Test case 1: A successful build log.
     * We create a dummy file containing "BUILD SUCCESS" and assert that the parser returns success=true.
     */
    @Test
    public void testProcessLog_Success() throws IOException {
        ProjectTester tester = new ProjectTester();

        File tempFile = File.createTempFile("mvn_success_log", ".txt");
        tempFile.deleteOnExit(); // Auto-delete when done

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[INFO] Scanning for projects...\n");
            writer.write("[INFO] ------------------------------------------------------------------------\n");
            writer.write("[INFO] BUILD SUCCESS\n"); // The magic words
            writer.write("[INFO] ------------------------------------------------------------------------\n");
        }

        // Simulate exit code 0: success
        ProjectTester.TestResults results = tester.processTestLog(tempFile, 0);

        assertTrue(results.success, "Should return true when log contains BUILD SUCCESS");
        assertEquals("All tests passed", results.message);
        assertTrue(results.failedTests.isEmpty(), "Failed tests list should be empty");
    }

    /**
     * Test case 2: A failed build log with specific test failures.
     * We write lines matching "[ERROR] Class.method:" and assert they are captured.
     */
    @Test
    public void testProcessLog_FailureWithSpecificTests() throws IOException {
        ProjectTester tester = new ProjectTester();

        File tempFile = File.createTempFile("mvn_failure_log", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[INFO] -------------------------------------------------------\n");
            writer.write("[INFO]  T E S T S\n");
            writer.write("[INFO] -------------------------------------------------------\n");
            writer.write("[ERROR] Failures: \n");
            writer.write("[ERROR]   CalculatorTest.testDivision:42 expected: <2> but was: <0>\n");
            writer.write("[ERROR]   UserAuthTest.testLogin:15 expected: <true> but was: <false>\n");
            writer.write("[INFO] -------------------------------------------------------\n");
            writer.write("[INFO] BUILD FAILURE\n");
        }

        // Simulate exit code 1 (failure)
        ProjectTester.TestResults results = tester.processTestLog(tempFile, 1);

        assertFalse(results.success, "Should return false for BUILD FAILURE");
        assertEquals(2, results.failedTests.size(), "Should detect exactly 2 failed tests");
        assertTrue(results.failedTests.contains("CalculatorTest.testDivision"));
        assertTrue(results.failedTests.contains("UserAuthTest.testLogin"));
        assertTrue(results.message.contains("Tests failed:"), "Message should start with 'Tests failed:'");
    }

    /**
     * Test Case 3: Compilation failure (Build fails, but no tests ran).
     */
    @Test
    public void testProcessLog_CompilationFailure() throws IOException {
        ProjectTester tester = new ProjectTester();

        File tempFile = File.createTempFile("mvn_compile_error", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("[INFO] ------------------------------------------------------------------------\n");
            writer.write("[ERROR] COMPILATION ERROR : \n");
            writer.write("[INFO] ------------------------------------------------------------------------\n");
            writer.write("[INFO] BUILD FAILURE\n");
        }

        // Simulate exit code 1
        ProjectTester.TestResults results = tester.processTestLog(tempFile, 1);

        assertFalse(results.success);
        assertTrue(results.failedTests.isEmpty(), "Should be empty if no tests actually ran");
        assertTrue(results.message.contains("Compilation or other error"), "Should give a generic error message");
    }

    /**
     * Very trivial test case for the runTests method, to ensure it handles a bad path.
     */
    @Test
    public void testRunTests_InvalidDirectory() {
        ProjectTester tester = new ProjectTester();

        // Run with a path that definitely doesn't exist
        ProjectTester.TestResults results = tester.runTests("path/that/does/not/exist");

        // Assert that it handled the error
        assertFalse(results.success, "Should return false for invalid directory");

        // Verify it caught the exception
        assertTrue(results.message.startsWith("System Error"),
                "Message should indicate a system error (IOException)");
    }
}
