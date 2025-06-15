package net.milkbowl.vault.tasks;

import net.milkbowl.vault.types.VersionInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class UpdateFetcherTask extends BukkitRunnable {

    private final Plugin plugin;
    private final Logger logger;
    private final VersionInfo versionInfo;

    public UpdateFetcherTask(Plugin plugin, VersionInfo versionInfo) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.versionInfo = versionInfo;
    }

    @Override
    public void run() {
        // Paper now handles this permission properly
        if (this.plugin.getConfig().getBoolean("update-check", true)) {
            try {
                this.logger.info("Checking for Updates ... ");
                double currentVersion = this.versionInfo.getCurrentVersion();
                double newVersion = updateCheck(currentVersion);
                if (newVersion > currentVersion) {
                    this.logger.warning("Stable Version: " + this.versionInfo.getNewVersionTitle() + " is out!" + " You are still running version: " + this.versionInfo.getCurrentVersionTitle());
                    this.logger.warning("Update at: https://github.com/SerlithNetwork/Safe");
                } else if (currentVersion > newVersion) {
                    this.logger.info("Stable Version: " + this.versionInfo.getNewVersionTitle() + " | Current Version: " + this.versionInfo.getCurrentVersionTitle());
                }
            } catch (Exception e) {
                // ignore exceptions
            }
        }
    }

    private double updateCheck(double currentVersion) {
        try {
            URL url = new URI("https://api.curseforge.com/servermods/files?projectids=33184").toURL();
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "Safe Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.isEmpty()) {
                this.logger.warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            // Pull the last version from the JSON
            this.versionInfo.setNewVersionTitle(((String) ((JSONObject) array.getLast()).get("name")).replace("Vault", "").replace("Safe", "").trim());
            return Double.parseDouble(this.versionInfo.getNewVersionTitle().replaceFirst("\\.", "").trim());
        } catch (Exception e) {
            this.logger.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }

}
