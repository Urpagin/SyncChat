package net.urpagin.syncchat;

import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

public class VersionCheck {
    private static final String LATEST_RELEASE_API_URL = "https://api.github.com/repos/Urpagin/SyncChat/releases/latest";
    private final String currentVersion;
    private String latestVersion;

    VersionCheck(String currentVersion) {
        this.currentVersion = currentVersion;

    }

    public boolean isUpToDate() throws Exception {
        String latestVersion = getLatestVersion().toLowerCase().replace("v", "");
        String currentVersion = this.currentVersion.toLowerCase();

        int[] latest = Arrays.stream(latestVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] current = Arrays.stream(currentVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();

        if (latest.length != 3 || current.length != 3) {
            throw new Exception("Failed to parse versions: wrong number of tokens");
        }

        for (int idx = 0; idx < 3; idx++) {
            if (current[idx] < latest[idx]) {
                return false; // Current is outdated
            } else if (current[idx] > latest[idx]) {
                return true; // Current is ahead
            }
        }
        return true; // Versions are the same
    }


    // Fetches the latest release name from GitHub API
    private String fetchLatestVersion() throws Exception {
        URL url = new URI(LATEST_RELEASE_API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = connection.getInputStream();
            String responseBody = new Scanner(responseStream, StandardCharsets.UTF_8)
                    .useDelimiter("\\A").next();
            connection.disconnect();

            // Parse JSON response
            JSONObject jsonObject = new JSONObject(responseBody);
            // The 'name' of the latest release
            return jsonObject.getString("tag_name");
        } else {
            throw new Exception("Error fetching the latest version. Did not get 200 OK.");
        }
    }


    public String getLatestVersion() throws Exception {
        if (latestVersion == null) {
            latestVersion = fetchLatestVersion();
        }
        return latestVersion;
    }
}
