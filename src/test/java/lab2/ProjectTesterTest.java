package lab2;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectTesterTest {
    /*
    Methods for testing success and fail cases of the project tester
     */

    @Test
    public void testResultsWithFail() throws IOException {
        ProjectTester tester = new ProjectTester();

        // Dummy log file
        File dummyLog = File.createTempFile("dummy_mvn_log_fail", ".txt");

        // Write mock fail content to the dummy
        try (FileWriter writer = new FileWriter(dummyLog)) {
            writer.write("INFO: Scanning for projects...\n");
            writer.write("-------------------------------------------------------\n");
            writer.write(" T E S T S\n");
            writer.write("-------------------------------------------------------\n");
            writer.write("Tests run: 5, Failures: 1, Errors: 0, Skipped: 0\n"); // The failure line
            writer.write("-------------------------------------------------------\n");
            writer.write("BUILD FAILURE\n");
        }
        // Try processing the mock test results, assert fail
        boolean result = tester.processTestResults(dummyLog);
        assertFalse(result, "Tester should detect failure in the log file");
        dummyLog.delete();
    }

    @Test
    public void testResultsWithSuccess() throws IOException {
        ProjectTester tester = new ProjectTester();

        // Dummy log file
        File dummyLog = File.createTempFile("dummy_mvn_log_success", ".txt");

        // Write mock success content to the dummy
        try (FileWriter writer = new FileWriter(dummyLog)) {
            writer.write("Tests run: 5, Failures: 0, Errors: 0, Skipped: 0\n");
            writer.write("[INFO] ------------------------------------------------------------------------\n");
            writer.write("[INFO] BUILD SUCCESS\n"); // The success line
            writer.write("[INFO] ------------------------------------------------------------------------\n");
        }
        // Try processing the mock test results, assert success
        boolean result = tester.processTestResults(dummyLog);
        assertTrue(result, "Tester should return true for BUILD SUCCESS");
        dummyLog.delete();
    }
}