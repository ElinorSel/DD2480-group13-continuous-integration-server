package lab2;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Handles storage and retrieval of CI build history.
 * Persists build records to disk so they survive server restarts.
 * Each build gets a unique ID and URL for accessing build details.
 */
public class HistoryHandler {

    private static final String HISTORY_DIR = "./builds";

    /**
     * Constructs a HistoryHandler and creates the build directory if it doesn't exist
     */
    public HistoryHandler() {
        // Create the directory if it doesn't exist
        new File(HISTORY_DIR).mkdirs();
    }

    /**
     * Saves a build to a file with a unique timestamp-based ID.
     * @param commitSha The commit identifier
     * @param log The build log output
     * @param status "Success" or "Failure"
     * @return The Build ID (timestamp), or null if save failed
     */
    public String saveBuild(String commitSha, String log, String status) {
        String buildId = commitSha;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        File file = new File(HISTORY_DIR, buildId + ".txt");
        try (PrintWriter out = new PrintWriter(file)) {
            out.println(commitSha);
            out.println(date);
            out.println(status);
            out.println("LOG_START"); // Marker to separate metadata from logs
            out.print(log);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return buildId;
    }

    /**
     * Generates an HTML page listing all builds in reverse chronological order.
     * @return HTML string containing clickable links to all builds
     */
    public String getHistoryListHtml() {
        File dir = new File(HISTORY_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));

        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Build History</title></head><body>");
        html.append("<h1>Build History</h1><ul>");

        if (files != null && files.length > 0) {
            // Sort by newest first
            Arrays.sort(files, Collections.reverseOrder());

            for (File f : files) {
                String id = f.getName().replace(".txt", "");
                // Link format: /builds/<id>
                html.append("<li><a href='/builds/").append(id).append("'>Build ").append(id).append("</a></li>");
            }
        } else {
            html.append("<p>No builds found.</p>");
        }
        html.append("</ul></body></html>");
        return html.toString();
    }

    /**
     * Generates an HTML page displaying detailed information for a specific build.
     * @param buildId The unique build identifier
     * @return HTML string with build details, or error message if build not found
     */
    public String getBuildDetailHtml(String buildId) {
        File file = new File(HISTORY_DIR, buildId + ".txt");
        if (!file.exists()) {
            return "<html><body><h1>Build not found</h1></body></html>";
        }

        StringBuilder log = new StringBuilder();
        String commit = "Unknown", date = "Unknown", status = "Unknown";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            commit = br.readLine();
            date = br.readLine();
            status = br.readLine();

            // Skip the "LOG_START" line
            String line = br.readLine();

            // Read the rest as log
            while ((line = br.readLine()) != null) {
                log.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading build file.";
        }

        return "<html><body>" +
                "<h1>Build Details: " + buildId + "</h1>" +
                "<p><b>Commit:</b> " + commit + "</p>" +
                "<p><b>Date:</b> " + date + "</p>" +
                "<p><b>Status:</b> " + status + "</p>" +
                "<h2>Build Log:</h2>" +
                "<pre style='background:#eee; padding:10px;'>" + log.toString() + "</pre>" +
                "<br><a href='/builds'>&larr; Back to List</a>" +
                "</body></html>";
    }
}