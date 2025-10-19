# YouTrack Integration with Microsoft Teams (Sample)

Early concept of an Integration of YouTrack with Microsoft Teams in the form of a Java app that polls a YouTrack Cloud instance for new issues and sends notifications to a messenger client. This repository contains a minimal working prototype and a mock messenger implementation.

![demo](https://github.com/user-attachments/assets/e1cde3e9-6c78-4530-bf8a-da6ce5651bf9)


## Requirements

- Java 17 (or compatible JDK)
- Maven 3.6+

## Build

From the project root run:

```bash
mvn clean package
```

This will produce an executable JAR under `target/` named `youtrack-integration-1.0.0.jar` (the shade plugin bundles dependencies).

## Configuration

Two configuration points are used by the application:

- `config.properties` — simple properties file (included in repo) with these keys:
  - `youtrack.url` — base URL of your YouTrack instance (example: `https://myteam.youtrack.cloud`).
  - `poll.interval` — polling interval in seconds for the `poll` command.
  - `project.id` — YouTrack project id (example `0-1`) to use when creating issues (used by `create`).

- `YOUTRACK_TOKEN` — environment variable containing a YouTrack permanent token with the required scopes (issues read/create). Export it in your shell before running the app:

```bash
export YOUTRACK_TOKEN="perm:..."
```

Notes:

- The repository includes `src/main/java/cloud/youtrackintegration/MessengerClient.java` which is currently a mock that prints to stdout. This class is meant to be replaced or extended for future integration with Microsoft Teams (or any other messenger).
- The `YouTrackClient` uses the REST API endpoints (e.g., `/api/issues`) and expects JSON responses.

## Run

After building and exporting `YOUTRACK_TOKEN`, run the JAR with one of the following commands.

- Poll for new issues (runs indefinitely):

```bash
java -jar target/youtrack-integration-1.0.0.jar poll
```

The app will read `config.properties` from the current working directory. It will poll every `poll.interval` seconds and print (or forward) new issue notifications.

- Create a new issue quickly:

```bash
java -jar target/youtrack-integration-1.0.0.jar create "Issue summary here"
```

This will create an issue in the project configured by `project.id` using the provided summary.

## Implementing a real messenger

The included `MessengerClient` is a stub. To forward notifications to Microsoft Teams, implement `sendMarkdown(String markdown)` to POST a JSON payload to a Teams incoming webhook URL (or use the Microsoft Graph API for richer scenarios). Example steps:

1. Create an incoming webhook in the target Teams channel and note the webhook URL.
2. Replace the `sendMarkdown` body with an HTTP POST to the webhook URL, setting `Content-Type: application/json` and sending a JSON body like `{"text":"..."}` or the adaptive card payload you prefer.
3. Securely store the webhook URL (environment variable or secrets manager) and avoid committing it to source control.

## Troubleshooting

- If you see authentication errors, verify `YOUTRACK_TOKEN` is correct and has needed scopes.
- If API responses fail, check `youtrack.url` in `config.properties` and ensure the URL includes the correct host (no trailing slash required; the client expects base URL).
- For debugging, run the program from an interactive shell so you can see stdout logs from the mock messenger and the app.

## Notes and next steps

- Add logging instead of System.out for production readiness.
- Add unit tests for `YouTrackClient` (mock HTTP) and `NotificationPoller`.
- Add a concrete `MessengerClient` implementation that posts to Microsoft Teams and reads its configuration (webhook URL) from an environment variable.


