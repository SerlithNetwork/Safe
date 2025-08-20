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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.listener.VaultListener;
import net.milkbowl.vault.permission.Permission;
import net.milkbowl.vault.permission.plugins.Permission_SuperPerms;
import net.milkbowl.vault.tasks.UpdateFetcherTask;
import net.milkbowl.vault.types.VersionInfo;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class Vault extends JavaPlugin {

    private final VersionInfo versionInfo = new VersionInfo(0, "", 0, "");
    private ServicesManager servicesManager;

    private static Vault INSTANCE;
    public static Vault getInstance() {
        return INSTANCE;
    }

    private static Component PREFIX;
    public static Component getPrefix() {
        return PREFIX;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
        PREFIX = MiniMessage.miniMessage().deserialize("<#919191>[<gradient:#6314ff:#14b9ff>" + this.getName() + "</gradient>]</#919191> ");
    }

    @Override
    public void onDisable() {
        // Remove all Service Registrations
        getServer().getServicesManager().unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public void onEnable() {
        this.versionInfo.setCurrentVersionTitle(this.getPluginMeta().getVersion());
        this.versionInfo.setCurrentVersion(Double.parseDouble(this.versionInfo.getCurrentVersionTitle().replaceFirst("\\.", "")));

        // set defaults
        this.getConfig().addDefault("update-check", false);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.servicesManager = Bukkit.getServer().getServicesManager();
        this.servicesManager.register(Permission.class, new Permission_SuperPerms(this), this, ServicePriority.Lowest);

        this.getServer().getPluginManager().registerEvents(new VaultListener(this.versionInfo), this);

        // Schedule to check the version every 24 hours for an update. This is to update the most recent
        // version so if an admin reconnects they will be warned about newer versions.
        new UpdateFetcherTask(this, this.versionInfo).runTaskTimerAsynchronously(this, 0L, 1728000L);

        // Load up the Plugin metrics
        Metrics metrics = new Metrics(this, 887);
        this.findCustomData(metrics);

        this.getLogger().info(String.format("Enabled Version %s", this.getPluginMeta().getVersion()));
    }

    @SuppressWarnings("deprecation")
    private void findCustomData(Metrics metrics) {
        // Create our Economy Graph and Add our Economy plotters
        RegisteredServiceProvider<Economy> rspEcon = this.servicesManager.getRegistration(Economy.class);
        Economy econ = null;
        if (rspEcon != null) {
            econ = rspEcon.getProvider();
        }
        final String econName = econ != null ? econ.getName() : "No Economy";
        metrics.addCustomChart(new SimplePie("economy", () -> econName));

        // Create our Permission Graph and Add our permission Plotters
        final @Nullable RegisteredServiceProvider<Permission> registeredPermission = this.servicesManager.getRegistration(Permission.class);
        if (registeredPermission != null) {
            final String permName = registeredPermission.getProvider().getName();
            metrics.addCustomChart(new SimplePie("permission", () -> permName));
        }

        // Create our Chat Graph and Add our chat Plotters
        RegisteredServiceProvider<Chat> rspChat = this.servicesManager.getRegistration(Chat.class);
        Chat chat = null;
        if (rspChat != null) {
            chat = rspChat.getProvider();
        }
        final String chatName = chat != null ? chat.getName() : "No Chat";
        metrics.addCustomChart(new SimplePie("chat", () -> chatName));
    }

    @SuppressWarnings("deprecation")
    public Collection<RegisteredServiceProvider<Economy>> getLegacyEconomyProviders() {
        return this.getServer().getServicesManager().getRegistrations(Economy.class);
    }

    public Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> getModernEconomyProviders() {
        return this.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);
    }

}
