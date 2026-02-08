package lab2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
 
import java.io.IOException;
 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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
    String state = "";
    String targetUrl = "";
    String description = "";

    public static void getData(){

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

        System.out.println(target);


        // here you do all the continuous integration tasks:
        // 1st clone your repository
        // 2nd compile the code
        // 3rd run the tests
        // 4th send the status to the GitHub API
        // 5th save to the build history
        
        //SendStatus.sendingStatus(); // String owner, String repo, String sha, String state, String targetUrl, String description





        response.getWriter().println("CI job done");
    }
 
    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }
}
