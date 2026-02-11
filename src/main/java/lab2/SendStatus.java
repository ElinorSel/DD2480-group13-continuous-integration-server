package lab2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
     * GitHub Commit Status Updater.
     * Sends a POST request to the GitHub REST API to update the status (success/failure/pending/error) 
     * of a specific commit SHA. This allows the CI server to report build results directly to the repo.
     * * Inputs: 
     * - repo owner username, repo name:repository identifiers.
     * - sha: commit hash to update.
     * - state: status state ("success", "failure", "error", or "pending").
     * - targetUrl: URL where users can see full build logs.
     * - description: short description of the result (e.g., "Build passed").
     * - (Implicit): System Environment Variable "GITHUB_TOKEN" for authentication.
     * * Returns: true if the status was successfully created (HTTP 201), false otherwise (even for invalid data input).
     */

public class SendStatus {
    public static boolean sendingStatus(String owner, String repo, String sha, String state, String targetUrl, String description) throws URISyntaxException {

    // URL (destination) which JSON string (message) will be sent to
    String url = "https://api.github.com/repos/" + owner + "/" + repo + "/statuses/" + sha;
    String message = "{\"state\": \"" + state + "\"," +
                        "\"target_url\": \"" + targetUrl + "\"," +
                        "\"description\": \"" + description + "\"," +
                        "\"context\": \"default\"}";
    String token = System.getenv("GITHUB_TOKEN");

    // github token debug:
    if (token == null || token.isEmpty()) {
        System.out.println("Error: GITHUB_TOKEN is not set.");
        return false; 
    } 
    
    try {
        URL object = new URI(url).toURL(); 

        HttpURLConnection connection = (HttpURLConnection) object.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
            os.writeBytes(message);
            os.flush();
        }

        int responseCode = connection.getResponseCode();

        // HTTP response 201 = successful
        if (responseCode == 201) {
            System.out.println("The following status was sent to the GitHub API: " + state);
            return true;
        }  
        else {
            System.err.println("Error: HTTP Response code - " + responseCode);
            return false;
        }
    }
    catch (IOException e) {
        // If any error occurs during api calls, e.g. invalid data, it will go into catch block
        e.printStackTrace();
        return false;
    }

    }
  
}

