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
    String owner = "";
    String repo = "";
    String sha = "";
    String state = "pending"; // TODO: for testing purposes
    String targetUrl = "http://example.com";
    String description = "this is a test description";
    String cloneUrl = "";
    String branch = "";

    /** returns the payload from the request as a string */
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

        // Debug: print first 500 characters of payload
        System.out.println("Payload preview: " + payloadString.substring(0, Math.min(50000, payloadString.length())));
        return payloadString;

    }

    /** parses the JSON string and extracts the owner, repo, sha, and clone url from the payload */
    public void parseJSON(String payloadString){
        try{
            if (payloadString.isEmpty()) {
                System.out.println("No payload received");
                return;
            }
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
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {

        //0. set the response type and status
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        // 1. parse the webhook payload
        String payloadString = payloadToString(request);
        parseJSON(payloadString);
        //TODO:
        // SET targetUrl to the url of the build history
        //SET description to the description of the build result
        //SET state to the state of the build result

        // 2. clone your repository and compile the code

        // ProjectBuilder build = new ProjectBuilder(cloneUrl, "main", sha ); //TODO: find the branch name

        // 3rd run the tests
        //ProjectTester.test()



        // 4th send the status to the GitHub API
        try {
            SendStatus.sendingStatus(owner, repo, sha, state, targetUrl, description);
        } catch (java.net.URISyntaxException e) {
            System.err.println("Error sending status: " + e.getMessage());
            e.printStackTrace();
        }

        // 5th save to the build history
        //TODO: to be implemented, unsure what it will need, build result and some basic info like date etc
        //HistoryHandler.saveBuild(owner, repo, sha, state, targetUrl, description, buildResult);

        response.getWriter().println("CI job done"); //shown in the browser
    }
 
    // used to start the CI server in command line
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
