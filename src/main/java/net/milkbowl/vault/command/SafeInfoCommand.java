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
        //VaultUnlocked Plugins
        final StringBuilder registeredModernEcons = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = this.plugin.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {

            if(!registeredModernEcons.isEmpty()) registeredModernEcons.append(", ");
            registeredModernEcons.append(econ.getProvider().getName());
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
        //VaultUnlocked Plugins
        final StringBuilder registeredModernPerms = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission>> perms2 = this.plugin.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.permission.Permission.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> perm : perms2) {

            if(!registeredModernPerms.isEmpty()) registeredModernPerms.append(", ");
            registeredModernPerms.append(perm.getProvider().getName());
        }


        // Get String of Registered Chat Services
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
        //VaultUnlocked Plugins
        final StringBuilder registeredModernChats = new StringBuilder();
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat>> chats2 = this.plugin.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.chat.Chat.class);
        for (RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> chat : chats2) {

            if(registeredModernChats.length() > 0) registeredModernChats.append(", ");
            registeredModernChats.append(chat.getProvider().getName());
        }


        // Get Economy & Permission primary Services
        RegisteredServiceProvider<Economy> rsp = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ = null;
        if (rsp != null) {
            econ = rsp.getProvider();
        }
        //VaultUnlocked
        net.milkbowl.vault2.economy.Economy econ2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> rsp2 = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault2.economy.Economy.class);
        if (rsp2 != null) {
            econ2 = rsp2.getProvider();
        }

        Permission perm = null;
        RegisteredServiceProvider<Permission> rspp = this.plugin.getServer().getServicesManager().getRegistration(Permission.class);
        if (rspp != null) {
            perm = rspp.getProvider();
        }
        //VaultUnlocked
        net.milkbowl.vault2.permission.Permission perm2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> rspp2 = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault2.permission.Permission.class);
        if (rspp2 != null) {
            perm2 = rspp2.getProvider();
        }

        Chat chat = null;
        RegisteredServiceProvider<Chat> rspc = this.plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if (rspc != null) {
            chat = rspc.getProvider();
        }
        //VaultUnlocked
        net.milkbowl.vault2.chat.Chat chat2 = null;
        final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> rspc2 = this.plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault2.chat.Chat.class);
        if (rspc2 != null) {
            chat2 = rspc2.getProvider();
        }


        // Send user some info!
        sender.sendMessage(String.format("[%s] Safe v%s Information", this.plugin.getName(), this.plugin.getPluginMeta().getVersion()));
        sender.sendMessage(String.format("[%s] Economy Legacy: %s [%s]", this.plugin.getName(), econ == null ? "None" : econ.getName(), registeredEcons == null ? "None" : registeredEcons.toString()));
        sender.sendMessage(String.format("[%s] Economy Modern: %s [%s]", this.plugin.getName(), econ2 == null ? "None" : econ2.getName(), registeredModernEcons.isEmpty() ? "None" : registeredModernEcons.toString()));
        sender.sendMessage(String.format("[%s] Permission Legacy: %s [%s]", this.plugin.getName(), perm == null ? "None" : perm.getName(), registeredPerms == null ? "None" : registeredPerms.toString()));
        sender.sendMessage(String.format("[%s] Permission Modern: %s [%s]", this.plugin.getName(), perm2 == null ? "None" : perm2.getName(), registeredModernPerms.isEmpty() ? "None" : registeredModernPerms.toString()));
        sender.sendMessage(String.format("[%s] Chat Legacy: %s [%s]", this.plugin.getName(), chat == null ? "None" : chat.getName(), registeredChats == null ? "None" : registeredChats.toString()));
        sender.sendMessage(String.format("[%s] Chat Modern: %s [%s]", this.plugin.getName(), chat2 == null ? "None" : chat2.getName(), registeredModernChats.isEmpty() ? "None" : registeredModernChats.toString()));

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
