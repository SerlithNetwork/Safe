package net.milkbowl.vault.command;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SafeInfoCommand extends Command implements PluginIdentifiableCommand {

    private final @NotNull Vault plugin;

    public SafeInfoCommand(@NotNull Vault plugin) {
        super("safe-info");
        this.plugin = plugin;

        this.setPermission("vault.admin");
        this.setUsage("/safe-info");
        this.setDescription("Displays information about Safe");
        this.setAliases(Collections.singletonList("vault-info"));

        this.plugin.getServer().getCommandMap().register("safe", this);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        // Get String of Registered Economy Services
        StringBuilder registeredEcons = null;
        Collection<RegisteredServiceProvider<Economy>> econs = this.plugin.getServer().getServicesManager().getRegistrations(Economy.class);
        for (RegisteredServiceProvider<Economy> econ : econs) {
            Economy e = econ.getProvider();
            if (registeredEcons == null) {
                registeredEcons = new StringBuilder(e.getName());
            } else {
                registeredEcons.append(", ").append(e.getName());
            }
        }

        // Get String of Registered Permission Services
        StringBuilder registeredPerms = null;
        Collection<RegisteredServiceProvider<Permission>> perms = this.plugin.getServer().getServicesManager().getRegistrations(Permission.class);
        for (RegisteredServiceProvider<Permission> perm : perms) {
            Permission p = perm.getProvider();
            if (registeredPerms == null) {
                registeredPerms = new StringBuilder(p.getName());
            } else {
                registeredPerms.append(", ").append(p.getName());
            }
        }

        StringBuilder registeredChats = null;
        Collection<RegisteredServiceProvider<Chat>> chats = this.plugin.getServer().getServicesManager().getRegistrations(Chat.class);
        for (RegisteredServiceProvider<Chat> chat : chats) {
            Chat c = chat.getProvider();
            if (registeredChats == null) {
                registeredChats = new StringBuilder(c.getName());
            } else {
                registeredChats.append(", ").append(c.getName());
            }
        }

        // Get Economy & Permission primary Services
        RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rsp != null) {
            econ = rsp.getProvider();
        }
        Permission perm = null;
        RegisteredServiceProvider<Permission> rspp = this.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }
        Chat chat = null;
        RegisteredServiceProvider<Chat> rspc = this.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }
        // Send user some info!
        sender.sendMessage(String.format("[%s] Safe v%s Information", this.plugin.getName(), this.plugin.getPluginMeta().getVersion()));
        sender.sendMessage(String.format("[%s] Economy: %s [%s]", this.plugin.getName(), econ == null ? "None" : econ.getName(), registeredEcons == null ? "None" : registeredEcons.toString()));
        sender.sendMessage(String.format("[%s] Permission: %s [%s]", this.plugin.getName(), perm == null ? "None" : perm.getName(), registeredPerms == null ? "None" : registeredPerms.toString()));
        sender.sendMessage(String.format("[%s] Chat: %s [%s]", this.plugin.getName(), chat == null ? "None" : chat.getName(), registeredChats == null ? "None" : registeredChats.toString()));

        return true;
    }

    private final List<String> empty = Collections.emptyList();
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return this.empty;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

}
