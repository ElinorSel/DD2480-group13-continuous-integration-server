package lab2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//importing the SendStatus class
import lab2.SendStatus;
//import lab2.ProjectBuilder;

/** 
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    //declare class variables
    private final HistoryHandler historyHandler = new HistoryHandler();

    String owner = "";
    String repo = "";
    String sha = "";
    String state = ""; 
    String targetUrl = "http://localhost:8080/builds";
    String description = "";
    String cloneUrl = "";
    String branch = "";

    /**
     * Reads the HTTP request body and returns the payload as a {@link String}.
     * <p>
     * For non-{POST} requests, this method logs a message and returns an empty string,
     * since only POST requests are expected from the GitHub webhook.
     *
     * @param request the incoming servlet request
     * @return the raw request body as a string, or an empty string on non-POST or read error
     */
    public String payloadToString(HttpServletRequest request){
        if (!"POST".equals(request.getMethod())) {
            System.out.println("Not a POST request, skipping JSON parsing");
            return "";
        }

        // Read the entire request body
        StringBuilder payload = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                payload.append(line).append("\n");
            }
        }
        catch (Exception e) {
            System.err.println("Error reading request body: " + e.getMessage());
            return "";
        }
        String payloadString = payload.toString().trim();

        // Debug: print payload
        System.out.println("Payload preview: " + payloadString.substring(0, Math.min(50000, payloadString.length())));
        return payloadString;

    }

    /**
     * Parses a GitHub webhook JSON payload and extracts repository and commit information.
     * <p>
     * On success, this method populates the instance fields {#owner}, {#repo},
     * {#sha}, {#cloneUrl} and {#branch}. If the payload is empty, an
     * {@link Exception} is thrown to signal an invalid webhook call.
     *
     * @param payloadString the JSON payload received from GitHub
     * @throws Exception if the payload is empty or cannot be processed according to the contract
     */
    public void parseJSON(String payloadString) throws Exception {
        
        if (payloadString.isEmpty()) {
            System.out.println("No payload received");
            throw new Exception("No payload received");
        }

        try{
            JsonObject json = JsonParser.parseString(payloadString).getAsJsonObject();
            String ref = json.get("ref").getAsString();
            if (ref == null) {
                System.err.println("No 'ref' field in JSON, no branch name found");
                return;
            }
            branch = ref.substring(ref.lastIndexOf('/') + 1);
            JsonObject repository = json.getAsJsonObject("repository");
            if (repository == null) {
                System.err.println("No 'repository' field in JSON");
                return;
            }
            
            owner = repository.getAsJsonObject("owner").get("login").getAsString();
            repo = repository.get("name").getAsString();
            
            // Get SHA - try "after" first (for push events), then "head_commit"
            if (json.has("after") && !json.get("after").getAsString().equals("0000000000000000000000000000000000000000")) {
                sha = json.get("after").getAsString();
            } else if (json.has("head_commit") && json.get("head_commit") != null) {
                sha = json.getAsJsonObject("head_commit").get("id").getAsString();
            } else {
                System.err.println("No commit SHA found in payload");
                return;
            }
            
            cloneUrl = repository.get("clone_url").getAsString();
            System.out.println("-------------------------------------");
            System.out.println("Successfully extracted: owner=" + owner + ", repo=" + repo + ", sha=" + sha + ", cloneUrl=" + cloneUrl + ", branch=" + branch);
        }
         catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            System.err.println("Payload was: " + payloadString.substring(0, Math.min(10000, payloadString.length())));
            e.printStackTrace();
        }

    }
    /**
     * Primary Jetty handler method invoked for every incoming HTTP request.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>Serves build history list at {@code /builds}.</li>
     *   <li>Serves individual build details at {@code /builds/{id}}.</li>
     *   <li>For {@code POST} requests, treats the request as a GitHub webhook:
     *       parses the JSON payload, clones the repository, runs tests, sends a
     *       status back to GitHub, and records the build in history.</li>
     *   <li>For other routes/methods, returns a simple informational message.</li>
     * </ul>
     *
     * @param target      the target path of the HTTP request (e.g. {@code /builds})
     * @param baseRequest the Jetty base request object
     * @param request     the servlet request
     * @param response    the servlet response used to send data back to the client
     * @throws IOException      if an I/O error occurs while handling the request
     * @throws ServletException if the servlet container encounters an error
     */
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        // Set response headers
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        // Handle history routes first - return early to avoid any webhook/CI logic
        if (target.equals("/builds")) {
            response.getWriter().println(historyHandler.getHistoryListHtml());
            return;
        }

        if (target.startsWith("/builds/")) {
            String buildId = target.substring("/builds/".length());
            response.getWriter().println(historyHandler.getBuildDetailHtml(buildId));
            return;
        }

        // Only process webhook/CI logic for POST requests
        if ("POST".equals(request.getMethod())) {
            // 1. parse the webhook payload
            String payloadString = payloadToString(request);
            try {
                parseJSON(payloadString);
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                e.printStackTrace();
                response.getWriter().println("Error: Failed to parse payload");
                return;
            }

            //SET description to the description of the build result

            // 2. clone your repository and compile the code
            ProjectBuilder build = new ProjectBuilder(cloneUrl, branch, sha );

            // 3. Run the tests and store report
            ProjectTester test = new ProjectTester();
            ProjectTester.TestResults results = test.runTests(build.localDir.getAbsolutePath());
            System.out.println(results.message); // "All tests passed" or "Failures: ... "
            
            // Set GitHub state based on test results (must be "success", "failure", "error", or "pending")
            state = results.success ? "success" : "failure";
            // Use the detailed message for the description
            description = results.message;

            // 4. send the status to the GitHub API
            try {
                SendStatus.sendingStatus(owner, repo, sha, state, targetUrl, description);
            } catch (java.net.URISyntaxException e) {
                System.err.println("Error sending status: " + e.getMessage());
                e.printStackTrace();
            }

            // 5th save to the build history
            String buildLog = "Commit: " + sha + "\nBranch: " + branch + "\nResult: " + state;
            historyHandler.saveBuild(sha, buildLog, state);

            response.getWriter().println("CI job done"); //shown in the browser
            return;
        }

        // Default response for non-POST, non-builds routes
        response.getWriter().println("CI server is running. Send POST requests for webhook processing.");
    }
 
    /**
     * Application entry point used to start the CI server from the command line.
     * <p>
     * Creates and configures a Jetty {@link Server} instance on port {@code 8080},
     * registers this class as the request handler, and blocks while the server is running.
     *
     * @param args command-line arguments (currently ignored)
     * @throws Exception if Jetty fails to start or encounters a fatal error
     */
    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting CI server on port 8080...");
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        System.out.println("CI server started successfully! Listening on http://localhost:8080");
        server.join();
    }
}
