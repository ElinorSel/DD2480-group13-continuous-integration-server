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
- how notification has been implemented and unit-tested.

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

## dependencies

## Statement of contributions

### Member Contributions

**Elinor Selinder:**

**Omar Almassri:**
- Developed the Build History module (`HistoryHandler.java`), enabling the CI server to persist and retrieve build results from disk.
- Wrote unit tests (`HistoryHandlerTest.java`) to verify file persistence, HTML generation, and error handling for the history system.
- Responsible for the "Build History" section of `README.md`.

**Hannes Westerberg:**

**Helin Saeid:**

**Liza Aziz:**

## Essence standard
-  Essence standardLinks to an external site. v1.2) by evaluating the checklist on p. 52: In what state are you in? Why? What are obstacles to reach the next state?
## License
This project is licensed under the terms of the MIT license.
