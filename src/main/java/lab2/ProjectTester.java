package lab2;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ProjectTester {
    /*
    - Runs "mvn test" in specified directory.
    - Captures output from test to a temporary file.
    - Analyzes that file to determine success/failure.
     */

    // Create a logger for the class
    private static final Logger logger = Logger.getLogger(ProjectTester.class.getName());

    public boolean runTests(String projectPath) {
        File projectDir = new File(projectPath);
        File logFile = new File(projectDir, "test_output.log");

        // Setup ProcessBuilder to run mvn test
        ProcessBuilder pb = new ProcessBuilder("mvn", "test");
        pb.directory(projectDir);

        // Redirect output to a file so we can analyze it later
        pb.redirectOutput(logFile);
        pb.redirectErrorStream(true);  // Combine stderr and stdout

        try {
            Process process = pb.start();
            int exitCode = process.waitFor();

            // Log the exit code
            if (exitCode != 0) {
                logger.log(Level.WARNING, "Maven process finished with non-zero exit code: " + exitCode);
            } else {
                logger.log(Level.INFO, "Maven process finished successfully.");
            }

            // Parse log file
            return processTestResults(logFile);

        } catch (IOException | InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to run tests for path: " + projectPath, e);
            return false;  // Assume failure in case of crash
        }
    }

    public boolean processTestResults(File logFile) {
        // Regex patterns
        Pattern successPattern = Pattern.compile("BUILD SUCCESS");
        Pattern failPattern = Pattern.compile("Failures:\\s*[1-9]");

        try (BufferedReader br = new BufferedReader(new FileReader(logFile))){
            String line;
            while ((line = br.readLine()) != null) {

                // Check for failure
                Matcher failMatcher = failPattern.matcher(line);
                if (failMatcher.find()) {
                    return false;
                }
                // Check for success
                Matcher successMatcher = successPattern.matcher(line);
                if (successMatcher.find()) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not read log file: " + logFile.getAbsolutePath(), e);
        }
        return false;  // Default to fail
    }
}
