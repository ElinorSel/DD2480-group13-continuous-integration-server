package lab2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectTester {

    /**
       A container for the results
     */
    public static class TestResults {
        public final boolean success;
        public final String message; // Either "All tests passed" or "Failed: ..."
        public final List<String> failedTests; // The list of failed test names
        public final String logs;

        public TestResults(boolean success, String message, List<String> failedTests, String logs) {
            this.success = success;
            this.message = message;
            this.failedTests = failedTests;
            this.logs = logs;
        }
    }
    /**
       Runs mvn test and returns the results
     */
    public TestResults runTests(String projectPath) {
        File projectDir = new File(projectPath);
        File logFile = new File(projectDir, "mvn_test_output.log");

        // Prepare the command - use Windows-compatible invocation on Windows
        String[] command;
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            command = new String[]{"cmd.exe", "/c", "mvn", "test"};
        } else {
            command = new String[]{"mvn", "test"};
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(projectDir);
        pb.redirectOutput(logFile);
        pb.redirectErrorStream(true); // merge error output with standard output

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();
            return processTestLog(logFile, exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); // Simple error handling
            return new TestResults(false, "System Error: " + e.getMessage(), new ArrayList<>(), "");
        }
    }

    /**
       Scan the log file for "BUILD SUCCESS" or failures.
     */
    public TestResults processTestLog(File logFile, int exitCode) {
        List<String> failedTestNames = new ArrayList<>();
        boolean buildSuccess = false;

        StringBuilder logBuilder = new StringBuilder();

        // Regex patterns for success and failure
        Pattern successPattern = Pattern.compile("BUILD SUCCESS");
        Pattern failurePattern = Pattern.compile("\\[ERROR\\]\\s+([\\w]+)\\.([\\w]+):");

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = br.readLine()) != null) {

                logBuilder.append(line).append("\n");

                // Check success
                if (successPattern.matcher(line).find()) {
                    buildSuccess = true;
                }
                // Check failure
                Matcher failMatcher = failurePattern.matcher(line);
                if (failMatcher.find()) {
                    String testName = failMatcher.group(1) + "." + failMatcher.group(2);
                    failedTestNames.add(testName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new TestResults(false, "Log read error", failedTestNames);
        }

        String fullLog = logBuilder.toString();

        // Determine Final Status
        if (exitCode == 0 || buildSuccess) {
            return new TestResults(true, "All tests passed", failedTestNames, fullLog);
        }
        if (!failedTestNames.isEmpty()) {
            String msg = "Tests failed: " + String.join(", ", failedTestNames);
            return new TestResults(false, msg, failedTestNames, fullLog);
        }

        return new TestResults(false, "Build failed (Compilation or other error)", failedTestNames);
    }
}
