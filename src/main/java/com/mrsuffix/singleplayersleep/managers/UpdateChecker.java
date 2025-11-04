package com.mrsuffix.singleplayersleep.managers;

import com.mrsuffix.singleplayersleep.SinglePlayerSleep;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

/**
 * Checks for plugin updates from GitHub releases
 */
public class UpdateChecker {

    private final SinglePlayerSleep plugin;
    private String latestVersion;
    private boolean updateAvailable = false;

    public UpdateChecker(SinglePlayerSleep plugin) {
        this.plugin = plugin;
    }

    /**
     * Check for updates asynchronously
     */
    public void checkForUpdates() {
        if (!plugin.getConfigManager().isUpdateCheckerEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String repo = plugin.getConfigManager().getGitHubRepo();
                String apiUrl = "https://api.github.com/repos/" + repo + "/releases/latest";

                plugin.debugLog("Checking for updates from: " + apiUrl);

                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", "SinglePlayerSleep-UpdateChecker");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parse JSON response (simple parsing without external library)
                    String json = response.toString();
                    latestVersion = extractVersion(json);

                    if (latestVersion != null) {
                        String currentVersion = plugin.getDescription().getVersion();
                        updateAvailable = isNewerVersion(currentVersion, latestVersion);

                        if (updateAvailable) {
                            plugin.getLogger().warning("╔════════════════════════════════════════╗");
                            plugin.getLogger().warning("║   UPDATE AVAILABLE!                    ║");
                            plugin.getLogger().warning("║   Current: " + currentVersion + "                       ║");
                            plugin.getLogger().warning("║   Latest:  " + latestVersion + "                       ║");
                            plugin.getLogger().warning("║   Download: github.com/" + repo + " ║");
                            plugin.getLogger().warning("╚════════════════════════════════════════╝");
                        } else {
                            plugin.getLogger().info("Plugin is up to date! (v" + currentVersion + ")");
                        }
                    }
                } else {
                    plugin.debugLog("Failed to check for updates. Response code: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                plugin.debugLog("Error checking for updates: " + e.getMessage());
                plugin.getLogger().log(Level.WARNING, "Could not check for updates. This is not critical.", e);
            }
        });
    }

    /**
     * Extract version from GitHub API JSON response
     * @param json JSON response
     * @return Version string or null
     */
    private String extractVersion(String json) {
        try {
            // Simple JSON parsing for "tag_name":"v1.0.0"
            int tagIndex = json.indexOf("\"tag_name\"");
            if (tagIndex == -1) return null;

            int startQuote = json.indexOf("\"", tagIndex + 11);
            int endQuote = json.indexOf("\"", startQuote + 1);

            if (startQuote != -1 && endQuote != -1) {
                String version = json.substring(startQuote + 1, endQuote);
                // Remove 'v' prefix if present
                return version.startsWith("v") ? version.substring(1) : version;
            }
        } catch (Exception e) {
            plugin.debugLog("Error parsing version from JSON: " + e.getMessage());
        }
        return null;
    }

    /**
     * Compare two version strings
     * @param current Current version
     * @param latest Latest version
     * @return true if latest is newer than current
     */
    private boolean isNewerVersion(String current, String latest) {
        try {
            String[] currentParts = current.split("\\.");
            String[] latestParts = latest.split("\\.");

            int length = Math.max(currentParts.length, latestParts.length);

            for (int i = 0; i < length; i++) {
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

                if (latestPart > currentPart) {
                    return true;
                } else if (latestPart < currentPart) {
                    return false;
                }
            }
        } catch (Exception e) {
            plugin.debugLog("Error comparing versions: " + e.getMessage());
        }
        return false;
    }

    /**
     * Check if an update is available
     * @return true if update is available
     */
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    /**
     * Get the latest version string
     * @return Latest version or null
     */
    public String getLatestVersion() {
        return latestVersion;
    }
}
