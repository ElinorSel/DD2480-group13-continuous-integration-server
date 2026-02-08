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
    String state = "failure"; // TODO: for testing purposes
    String targetUrl = "http://example.com"; // Must be a valid URL (http:// or https://)
    String description = "example description";
    String cloneUrl = "";

    public void getData(HttpServletRequest request){
        // Check if this is a POST request (webhooks are POST)
        if (!"POST".equals(request.getMethod())) {
            System.out.println("Not a POST request, skipping JSON parsing");
            return;
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
            return;
        }

        String payloadString = payload.toString().trim();
        //check if the payload is empty
        if (payloadString.isEmpty()) {
            System.out.println("No payload received");
            return;
        }

        // Debug: print first 500 characters of payload
        System.out.println("Payload preview: " + payloadString.substring(0, Math.min(500, payloadString.length())));
        // Parse JSON
        
        try{
            JsonObject json = JsonParser.parseString(payloadString).getAsJsonObject();
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
            
            cloneUrl = repository.get("clone_url").getAsString(); //TODO: to be used in the cloner class
            System.out.println("Successfully extracted: owner=" + owner + ", repo=" + repo + ", sha=" + sha);
        }
         catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            System.err.println("Payload was: " + payloadString.substring(0, Math.min(500, payloadString.length())));
            e.printStackTrace();
        }


    }
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {


        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        getData(request);

        // here you do all the continuous integration tasks:
        // 1st clone your repository
        // 2nd compile the code
        // 3rd run the tests
        // 4th send the status to the GitHub API
        // 5th save to the build history

        //TODO:SET targetUrl to the url of the build history
        //TODO:SET description to the description of the build result
        //TODO:SET state to the state of the build result
        
        try {
            SendStatus.sendingStatus(owner, repo, sha, state, targetUrl, description);
        } catch (java.net.URISyntaxException e) {
            System.err.println("Error sending status: " + e.getMessage());
            e.printStackTrace();
        }
        




        response.getWriter().println("CI job done");
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
