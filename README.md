# KTH Course: DD2480 - Group 13 | LAB 2: Continuous Integration
A minimal continuous integration (CI) server. The project implements core CI features such as webhook-triggered builds, automated compilation and testing, and build result notifications, with a strong focus on software engineering practices including clear documentation, test coverage, and traceable collaboration.



## Prerequisites

To build and run this project, you will need:

* **Java:** Version 8
* **Maven:** Version 3.9.12

**OBS!** Newer version may also work, but this project has been specifically built and tested with the mentioned versions.

### Executing program


## Compilation
The compilation is implemented in the ProjectBuilder class. This class is also used to clone the repository which is implemented using JGit. If the cloning was successful the repository will be compiled which is done using the ProjectBuilder class. Finally if both processes are succesful the cloning method will return the repository. 

The unit test sends a repository from Github into the class which is cloned and compiled. We check that the cloning was done successfully by checking that the branch of the clone is equal to the branch that was sent into the method. 
## Test execution
- how test execution has been implemented and unit-tested.
## Notifications
The notification feature is implemented in the SendStatus class. It serves as the bridge between our CI server and GitHub that provides immediate visual feedback on the build result. We utilized the GitHub REST API (specifically the /statuses/{sha} endpoint) to update the commit status. Instead of using heavy external libraries, our implementation uses HttpURLConnection to send authenticated POST requests. 

The feature is tested in `SendStatusTest.java` using JUnit. We verify that sendingStatus() returns true when provided with valid credentials and a real commit SHA, which confirms the API accepts the request (HTTP 201). We test failure scenarios (e.g., invalid tokens or missing repositories) to ensure the system  handles errors without crashing (returning false and logging the error).

**OBS!** Security is managed via environment variables (GITHUB_TOKEN), ensuring no sensitive tokens are hardcoded.

To run the tests in `SendStatusTest.java` (which you can also confirm by looking at the chosen commit on GitHub), enter the following terminal command:
```
mvn compile
mvn test -Dtest=SendStatusTest
```

### Project Structure

## Dependencies
This project uses Maven for dependency management. All dependencies are defined in `pom.xml`.
## Statement of contributions

### Member Contributions

**Elinor Selinder:**

**Omar Almassri:**

**Hannes Westerberg:**

**Helin Saeid:**
- Developed the ProjectBuilder class which clones and compiles a repository. 
- Wrote a unit test to test the ProjectBuilder class with a test repository. 

**Liza Aziz:**

- Developed the SendStatus module (`SendStatus.java`), enabling the CI server to communicate build results back to GitHub via the REST API.
- Wrote unit tests (`SendStatusTest.java`) to verify API connectivity, token authentication and error handling for the notification system.
- Responsible for the "Notification" and "Essence standard" section of `README.md`.

## Essence standard
We have evaluated the checklist on page 52 of the Essence Standard and determined we are in state **"Collaborating"**. After having worked with each other for two projects, we have become more cohesive and understand the overall layout of the plan and how we as a team will contribute. We communicate openly about any technical issues that we have (e.g., in need of help to write code, token issues, uncertainty of assignment goal), and  we trust each other's code contributions. We are not yet in the "Performing" state because our delivery is not yet consistent, especially with our unaligned shedules. In our case, we often waste time during meetings due to unstructured discussions and/or lack of knowledge of the assignment goal. Additionally, we found that we frequently relied on external guidance to clarify the assignment agenda and requirements, rather than being able to resolve these uncertainties  internally. To reach the next state, we need to optimize our meeting structure to respect our time constraints and develop a better internal process for interpreting projects.
## License
This project is licensed under the terms of the MIT license.
