# KTH Course: DD2480 - Group 13 | LAB 2: Continuous Integration

A minimal continuous integration (CI) server. The project implements core CI features such as webhook-triggered builds, automated compilation and testing, and build result notifications, with a strong focus on software engineering practices including clear documentation, test coverage, and traceable collaboration.

**Build URL:** http://localhost:8080/builds

## Prerequisites

To build and run this project, you will need:

- **Java:** Version 8
- **Maven:** Version 3.9.12
- **ngrok:** Version 3.36.0

**OBS!** Newer version may also work, but this project has been specifically built and tested with the mentioned versions.

### Executing the Program

1. Start the tunnel: `ngrok http 8080`
2. Copy the ngrok forwarding URL (e.g. `https://abc123.ngrok.io`)
3. Add it as a webhook in your GitHub repo: **Settings → Webhooks → Add webhook**
   - Payload URL: `<your-ngrok-url>`
   - Content type: `application/json`
   - Trigger: "Just the push event"
4. In a new terminal, start the server: `mvn exec:java`

## CI Pipeline

The `ContinuousIntegrationServer` listens on port 8080 for incoming POST requests triggered by GitHub push events. When a commit is pushed to any branch, GitHub sends a POST request to the server's `/` endpoint. The server parses the JSON payload to extract the relevant commit details — including the branch name, commit SHA, author, and repository URL — and then orchestrates the following pipeline on that specific branch:

1. **Clone** the repository at the pushed branch using `ProjectBuilder`
2. **Compile** the project and report any compilation errors
3. **Test** the project by running `mvn test` via `ProjectTester`
4. **Notify** GitHub of the build result by updating the commit status via `SendStatus`
5. **Record** the build result to disk via `HistoryHandler` for persistent build history

## Compilation

The compilation is implemented in the ProjectBuilder class which is called by the ContinuousIntegrationServer when a commit triggers a webhook. This class is also used to clone the repository which is implemented using JGit. If the cloning was successful the repository will be compiled which is done using the ProjectBuilder class. Finally if both processes are succesful the cloning method will return the repository.

The unit test sends a repository from Github into the class which is cloned and compiled. We check that the cloning was done successfully by checking that the branch of the clone is equal to the branch that was sent into the method.

## Test execution

Test execution is implemented in the ProjectTester class by wrapping the 'mvn test' command with Java's ProcessBuilder. The console output is redirected to a log file and parsed using Regex to detect build success or failure. The method returns a TestResults object containing the overall status and a list of specific failed tests, if any.

We unit tested ProjectTester using dummy log files containing hardcoded output scenarios (successful build, compilation error, specific test failures). We then point the parser method at these files to verify it correctly identifies the status and extracts the names of failed tests.

## Notifications

The notification feature is implemented in the SendStatus class. It serves as the bridge between our CI server and GitHub that provides immediate visual feedback on the build result. We utilized the GitHub REST API (specifically the /statuses/{sha} endpoint) to update the commit status. Instead of using heavy external libraries, our implementation uses HttpURLConnection to send authenticated POST requests.

The feature is tested in `SendStatusTest.java` using JUnit. We verify that sendingStatus() returns true when provided with valid credentials and a real commit SHA, which confirms the API accepts the request (HTTP 201). We test failure scenarios (e.g., invalid tokens or missing repositories) to ensure the system handles errors without crashing (returning false and logging the error).

**OBS!** Security is managed via environment variables (GITHUB_TOKEN), ensuring no sensitive tokens are hardcoded.

To run the tests in `SendStatusTest.java` (which you can also confirm by looking at the chosen commit on GitHub), enter the following terminal command:

```
mvn compile
mvn test -Dtest=SendStatusTest
```

## Build History

The build history feature is implemented in the `HistoryHandler` class. It persists all CI build results to disk so they survive server restarts. Each build is saved as a text file in the `./builds/` directory with a unique timestamp-based ID, storing the commit SHA, build date, status, and full build logs.

### URLs

- **List all builds:** `http://localhost:8080/builds`
- **View specific build:** `http://localhost:8080/builds/{buildId}`

The feature is tested in `HistoryHandlerTest.java` using JUnit. We verify that builds are correctly saved to disk, that the detail page displays the correct information, that the history list contains clickable links, and that missing builds return an appropriate error message.

To run the tests in `HistoryHandlerTest.java`, enter the following terminal command:

```
mvn compile
mvn test -Dtest=HistoryHandlerTest
```

### Project Structure

## Dependencies

This project uses Maven for dependency management. All dependencies are defined in `pom.xml`.

## Statement of contributions

### Member Contributions

**Elinor Selinder:**

- Developed the main hander in (`ContinuousIntegrationServer.java`), which listenes for webhooks orchastrates calls to and in between the other classes which in turn clone, build, test, and record the history of builds.
- Wrote the tests for ContinousIntegrationServer.
- Setup ngrok and github webhooks to connect to the local CI server.

**Omar Almassri:**

- Developed the Build History module (`HistoryHandler.java`), enabling the CI server to persist and retrieve build results from disk.
- Wrote unit tests (`HistoryHandlerTest.java`) to verify file persistence, HTML generation, and error handling for the history system.
- Responsible for the "Build History" section of `README.md`.

**Hannes Westerberg:**

- Developed the ProjectTester class, automating the maven tests and creating a status report to send to GitHub.
- Wrote unit tests for the ProjectTester class.

**Helin Saeid:**

- Developed the ProjectBuilder class which clones and compiles a repository.
- Wrote a unit test to test the ProjectBuilder class with a test repository.

**Liza Aziz:**

- Developed the SendStatus module (`SendStatus.java`), enabling the CI server to communicate build results back to GitHub via the REST API.
- Wrote unit tests (`SendStatusTest.java`) to verify API connectivity, token authentication and error handling for the notification system.
- Responsible for the "Notification" and "Essence standard" section of `README.md`.

## Essence standard

We have evaluated the checklist on page 52 of the Essence Standard and determined we are in state **"Collaborating"**. After having worked with each other for two projects, we have become more cohesive and understand the overall layout of the plan and how we as a team will contribute. We communicate openly about any technical issues that we have (e.g., in need of help to write code, token issues, uncertainty of assignment goal), and we trust each other's code contributions. We are not yet in the "Performing" state because our delivery is not yet consistent, especially with our unaligned shedules. In our case, we often waste time during meetings due to unstructured discussions and/or lack of knowledge of the assignment goal. Additionally, we found that we frequently relied on external guidance to clarify the assignment agenda and requirements, rather than being able to resolve these uncertainties internally. To reach the next state, we need to optimize our meeting structure to respect our time constraints and develop a better internal process for interpreting projects.

## License

This project is licensed under the terms of the MIT license.
