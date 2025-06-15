/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import net.milkbowl.vault.command.SafeConvertCommand;
import net.milkbowl.vault.command.SafeInfoCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.annotation.Nullable;

public class Vault extends JavaPlugin {

    private static final String VAULT_BUKKIT_URL = "https://github.com/SerlithNetwork/Safe";
    private static Logger log;
    private String newVersionTitle = "";
    private double newVersion = 0;
    private double currentVersion = 0;
    private String currentVersionTitle = "";

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        log = this.getLogger();
        currentVersionTitle = this.getPluginMeta().getVersion();
        currentVersion = Double.parseDouble(currentVersionTitle.replaceFirst("\\.", ""));

        // set defaults
        getConfig().addDefault("update-check", false);
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new VaultListener(), this);
        new SafeInfoCommand(this);
        new SafeConvertCommand(this);

        // Schedule to check the version every 24 hours for an update. This is to update the most recent
        // version so if an admin reconnects they will be warned about newer versions.
        this.getServer().getScheduler().runTask(this, () -> {
            // Programmatically set the default permission value cause Bukkit doesn't handle plugin.yml properly for Load order STARTUP plugins
            org.bukkit.permissions.Permission perm = getServer().getPluginManager().getPermission("vault.update");
            if (perm == null)
            {
                perm = new org.bukkit.permissions.Permission("vault.update");
                perm.setDefault(PermissionDefault.OP);
                this.getServer().getPluginManager().addPermission(perm);
            }
            perm.setDescription("Allows a user or the console to check for vault updates");

            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                if (getServer().getConsoleSender().hasPermission("vault.update") && getConfig().getBoolean("update-check", true)) {
                    try {
                        log.info("Checking for Updates ... ");
                        newVersion = updateCheck(currentVersion);
                        if (newVersion > currentVersion) {
                            log.warning("Stable Version: " + newVersionTitle + " is out!" + " You are still running version: " + currentVersionTitle);
                            log.warning("Update at: https://dev.bukkit.org/projects/vault");
                        } else if (currentVersion > newVersion) {
                            log.info("Stable Version: " + newVersionTitle + " | Current Version: " + currentVersionTitle);
                        }
                    } catch (Exception e) {
                        // ignore exceptions
                    }
                }
            }, 0, 1728000L);

        });

        // Load up the Plugin metrics
        Metrics metrics = new Metrics(this, 887);
        findCustomData(metrics);

        log.info(String.format("Enabled Version %s", this.getPluginMeta().getVersion()));
    }

    public double updateCheck(double currentVersion) {
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectids=33184");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "Vault Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.isEmpty()) {
                this.getLogger().warning("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            // Pull the last version from the JSON
            newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).replace("Vault", "").trim();
            return Double.parseDouble(newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (Exception e) {
            log.info("There was an issue attempting to check for the latest version.");
        }
        return currentVersion;
    }

    private void findCustomData(Metrics metrics) {
        // Create our Economy Graph and Add our Economy plotters
        RegisteredServiceProvider<Economy> rspEcon = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }
        final String econName = econ != null ? econ.getName() : "No Economy";
        metrics.addCustomChart(new SimplePie("economy", () -> econName));

        // Create our Permission Graph and Add our permission Plotters
        final @Nullable RegisteredServiceProvider<Permission> registeredPermission = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (registeredPermission != null) {
            final String permName = registeredPermission.getProvider().getName();
            metrics.addCustomChart(new SimplePie("permission", () -> permName));
        }

        // Create our Chat Graph and Add our chat Plotters
        RegisteredServiceProvider<Chat> rspChat = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }
        final String chatName = chat != null ? chat.getName() : "No Chat";
        metrics.addCustomChart(new SimplePie("chat", () -> chatName));
    }

    public class VaultListener implements Listener {

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (player.hasPermission("vault.update")) {
                try {
                    if (newVersion > currentVersion) {
                        player.sendMessage("Vault " +  newVersionTitle + " is out! You are running " + currentVersionTitle);
                        player.sendMessage("Update Vault at: " + VAULT_BUKKIT_URL);
                    }
                } catch (Exception ignore) {}
            }
        }

    }
}
