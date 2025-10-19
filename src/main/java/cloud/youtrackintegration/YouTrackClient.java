package cloud.youtrackintegration;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.json.*;

public class YouTrackClient {
    private final String baseUrl;
    private final String token;
    private long lastTimestamp = 0;

    public YouTrackClient(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }

    private HttpURLConnection makeRequest(String endpoint) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/json");
        return conn;
    }

    public List<String> getNewIssues() throws IOException {
        String fields = "?fields=idReadable,summary,created,project(name),reporter(name)&$top=10";
        HttpURLConnection conn = makeRequest("/api/issues" + fields);
        conn.connect();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String json = br.lines().reduce("", (a, b) -> a + b);
            JSONArray arr = new JSONArray(json);

            List<String> messages = new ArrayList<>();
            long newest = lastTimestamp;

            for (int i = 0; i < arr.length(); i++) {
                JSONObject issue = arr.getJSONObject(i);
                long created = issue.optLong("created", 0);

                // Only report new issues (since last poll)
                if (created > lastTimestamp) {
                    String id = issue.optString("idReadable", "(no id)");
                    String summary = issue.optString("summary", "(no summary)");
                    String project = issue.optJSONObject("project") != null ? issue.getJSONObject("project").optString("name") : "Unknown";
                    String reporter = issue.optJSONObject("reporter") != null ? issue.getJSONObject("reporter").optString("name") : "Unknown";

                    String msg = String.format(
                        "**New issue %s**\n Project: %s\n Reporter: %s\n Summary: %s",
                        id, project, reporter, summary
                    );
                    messages.add(msg);
                    if (created > newest) newest = created;
                }
            }

            // Update timestamp to latest
            if (newest > lastTimestamp) {
                lastTimestamp = newest;
            }

            return messages;
        }
    }

    public void createIssue(String projectId, String summary) throws IOException {
        URL url = new URL(baseUrl + "/api/issues");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = new JSONObject()
            .put("project", new JSONObject().put("id", projectId))
            .put("summary", summary)
            .put("description", "Let'\''s create a new issue using YouTrack'\''s REST API.")
            .toString();

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() != 200 && conn.getResponseCode() != 201) {
            throw new IOException("Error: " + conn.getResponseCode());
        }

        System.out.println("Issue created: " + summary);
    }
}
