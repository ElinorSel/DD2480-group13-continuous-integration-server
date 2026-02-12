package lab2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * GitHub Commit Status Updater.
 * <p>
 * This class is responsible for sending POST requests to the GitHub REST API to update 
 * the status of a specific commit. It allows the CI server to report build results 
 * (success, failure, pending, or error) directly to the repository interface.
 * </p>
 */
public class SendStatus {
    
    /**
     * Sends a status update to a specific commit on GitHub.
     * <p>
     * This method constructs a JSON payload and sends it via HTTP POST.
     * It requires the system environment variable <code>GITHUB_TOKEN</code> to be set for authentication.
     * </p>
     *
     * @param owner       The username of the repository owner (e.g., "Group13").
     * @param repo        The name of the repository.
     * @param sha         The specific commit hash (SHA) to update.
     * @param state       The status state: "success", "failure", "error", or "pending".
     * @param targetUrl   The URL where users can view the full build logs.
     * @param description A short description of the result (e.g., "Build passed").
     * @return            true if the status was successfully created (HTTP 201), false otherwise.
     * @throws URISyntaxException If the constructed URL for the API endpoint is invalid.
     */
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

