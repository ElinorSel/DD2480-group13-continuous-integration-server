# KTH Course: DD2480 - Group 13 | LAB 2: Continuous Integration
A minimal continuous integration (CI) server. The project implements core CI features such as webhook-triggered builds, automated compilation and testing, and build result notifications, with a strong focus on software engineering practices including clear documentation, test coverage, and traceable collaboration.



## Prerequisites

To build and run this project, you will need:

* **Java:** Version 8
* **Maven:** Version 3.9.12

**OBS!** Newer version may also work, but this project has been specifically built and tested with the mentioned versions.

### Executing program


## Compilation
- how compilation has been implemented and unit-tested.
## Test execution
- how test execution has been implemented and unit-tested.
## Notifications
The notification feature is implemented in the SendStatus class. It serves as the bridge between our CI server and GitHub that provides immediate visual feedback on the build result. We utilized the GitHub REST API (specifically the /statuses/{sha} endpoint) to update the commit status. Instead of using heavy external libraries, the implementation uses Java's native HttpURLConnection to send authenticated POST requests. The system dynamically constructs a JSON payload containing:

The feature is tested in `SendStatusTest.java` using JUnit. We verify that sendingStatus() returns true when provided with valid credentials and a real commit SHA, which confirms the API accepts the request (HTTP 201). We test failure scenarios (e.g., invalid tokens or missing repositories) to ensure the system  handles errors without crashing (returning false and logging the error).

**OBS!** Security is managed via environment variables (GITHUB_TOKEN), ensuring no sensitive tokens are hardcoded.

To run the tests in `SendStatusTest.java` (which you can also confirm by looking at the chosen commit to is tested), enter the following terminal command:
```
mvn compile
mvn test -Dtest=SendStatusTest
```

### Project Structure

## Dependencies

## Statement of contributions

### Member Contributions

**Elinor Selinder:**

**Omar Almassri:**

**Hannes Westerberg:**

**Helin Saeid:**

**Liza Aziz:**

- Developed the SendStatus module (`SendStatus.java`), enabling the CI server to communicate build results back to GitHub via the REST API.
- Wrote unit tests (`SendStatusTest.java`) to verify API connectivity, token authentication and error handling for the notification system.
- Responsible for the "Notification" section of `README.md`.

## Essence standard
-  Essence standardLinks to an external site. v1.2) by evaluating the checklist on p. 52: In what state are you in? Why? What are obstacles to reach the next state?
## License
This project is licensed under the terms of the MIT license.
