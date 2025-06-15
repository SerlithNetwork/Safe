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

import io.papermc.paper.command.brigadier.Commands;
import net.milkbowl.vault.command.SafeConvertCommand;
import net.milkbowl.vault.command.SafeInfoCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.listener.VaultListener;
import net.milkbowl.vault.permission.Permission;

import net.milkbowl.vault.tasks.UpdateFetcherTask;
import net.milkbowl.vault.types.VersionInfo;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public class Vault extends JavaPlugin {

    private final VersionInfo versionInfo = new VersionInfo(0, "", 0, "");

    @Override
    public void onLoad() {
        Commands.literal("");
    }

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        this.versionInfo.setCurrentVersionTitle(this.getPluginMeta().getVersion());
        this.versionInfo.setCurrentVersion(Double.parseDouble(this.versionInfo.getCurrentVersionTitle().replaceFirst("\\.", "")));

        // set defaults
        getConfig().addDefault("update-check", false);
        getConfig().options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new VaultListener(this.versionInfo), this);
        new SafeInfoCommand(this);
        new SafeConvertCommand(this);

        // Schedule to check the version every 24 hours for an update. This is to update the most recent
        // version so if an admin reconnects they will be warned about newer versions.
        new UpdateFetcherTask(this, this.versionInfo).runTaskTimerAsynchronously(this, 0L, 1728000L);

        // Load up the Plugin metrics
        Metrics metrics = new Metrics(this, 887);
        findCustomData(metrics);

        this.getLogger().info(String.format("Enabled Version %s", this.getPluginMeta().getVersion()));
    }

    @SuppressWarnings("deprecation")
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

}
