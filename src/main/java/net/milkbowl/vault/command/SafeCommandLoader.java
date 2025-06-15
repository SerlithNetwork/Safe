package net.milkbowl.vault.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class SafeCommandLoader {

    @SuppressWarnings("deprecation")
    public LiteralCommandNode<CommandSourceStack> buildInfoCommand() {
        return Commands.literal("safe-info")
                .requires(s -> s.getSender().hasPermission("vault.admin"))
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();

                    // Get String of Registered Economy Services
                    StringBuilder registeredEcons = null;
                    Collection<RegisteredServiceProvider<Economy>> econs = Vault.getInstance().getServer().getServicesManager().getRegistrations(Economy.class);
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
                    final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = Vault.getInstance().getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);
                    for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {

                        if(!registeredModernEcons.isEmpty()) registeredModernEcons.append(", ");
                        registeredModernEcons.append(econ.getProvider().getName());
                    }


                    // Get String of Registered Permission Services
                    StringBuilder registeredPerms = null;
                    Collection<RegisteredServiceProvider<Permission>> perms = Vault.getInstance().getServer().getServicesManager().getRegistrations(Permission.class);
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
                    final Collection<RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission>> perms2 = Vault.getInstance().getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.permission.Permission.class);
                    for (RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> perm : perms2) {

                        if(!registeredModernPerms.isEmpty()) registeredModernPerms.append(", ");
                        registeredModernPerms.append(perm.getProvider().getName());
                    }


                    // Get String of Registered Chat Services
                    StringBuilder registeredChats = null;
                    Collection<RegisteredServiceProvider<Chat>> chats = Vault.getInstance().getServer().getServicesManager().getRegistrations(Chat.class);
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
                    final Collection<RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat>> chats2 = Vault.getInstance().getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.chat.Chat.class);
                    for (RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> chat : chats2) {

                        if(!registeredModernChats.isEmpty()) registeredModernChats.append(", ");
                        registeredModernChats.append(chat.getProvider().getName());
                    }


                    // Get Economy & Permission primary Services
                    RegisteredServiceProvider<Economy> rsp = Vault.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
                    Economy econ = null;
                    if (rsp != null) {
                        econ = rsp.getProvider();
                    }
                    //VaultUnlocked
                    net.milkbowl.vault2.economy.Economy econ2 = null;
                    final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> rsp2 = Vault.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault2.economy.Economy.class);
                    if (rsp2 != null) {
                        econ2 = rsp2.getProvider();
                    }

                    Permission perm = null;
                    RegisteredServiceProvider<Permission> rspp = Vault.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
                    if (rspp != null) {
                        perm = rspp.getProvider();
                    }
                    //VaultUnlocked
                    net.milkbowl.vault2.permission.Permission perm2 = null;
                    final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> rspp2 = Vault.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault2.permission.Permission.class);
                    if (rspp2 != null) {
                        perm2 = rspp2.getProvider();
                    }

                    Chat chat = null;
                    RegisteredServiceProvider<Chat> rspc = Vault.getInstance().getServer().getServicesManager().getRegistration(Chat.class);
                    if (rspc != null) {
                        chat = rspc.getProvider();
                    }
                    //VaultUnlocked
                    net.milkbowl.vault2.chat.Chat chat2 = null;
                    final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> rspc2 = Vault.getInstance().getServer().getServicesManager().getRegistration(net.milkbowl.vault2.chat.Chat.class);
                    if (rspc2 != null) {
                        chat2 = rspc2.getProvider();
                    }


                    // Send user some info!
                    sender.sendMessage(String.format("[%s] Safe v%s Information", Vault.getInstance().getName(), Vault.getInstance().getPluginMeta().getVersion()));
                    sender.sendMessage(String.format("[%s] Economy Legacy: %s [%s]", Vault.getInstance().getName(), econ == null ? "None" : econ.getName(), registeredEcons == null ? "None" : registeredEcons.toString()));
                    sender.sendMessage(String.format("[%s] Economy Modern: %s [%s]", Vault.getInstance().getName(), econ2 == null ? "None" : econ2.getName(), registeredModernEcons.isEmpty() ? "None" : registeredModernEcons.toString()));
                    sender.sendMessage(String.format("[%s] Permission Legacy: %s [%s]", Vault.getInstance().getName(), perm == null ? "None" : perm.getName(), registeredPerms == null ? "None" : registeredPerms.toString()));
                    sender.sendMessage(String.format("[%s] Permission Modern: %s [%s]", Vault.getInstance().getName(), perm2 == null ? "None" : perm2.getName(), registeredModernPerms.isEmpty() ? "None" : registeredModernPerms.toString()));
                    sender.sendMessage(String.format("[%s] Chat Legacy: %s [%s]", Vault.getInstance().getName(), chat == null ? "None" : chat.getName(), registeredChats == null ? "None" : registeredChats.toString()));
                    sender.sendMessage(String.format("[%s] Chat Modern: %s [%s]", Vault.getInstance().getName(), chat2 == null ? "None" : chat2.getName(), registeredModernChats.isEmpty() ? "None" : registeredModernChats.toString()));

                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }


    @SuppressWarnings({"deprecation", "DuplicatedCode"})
    public LiteralCommandNode<CommandSourceStack> buildConvertCommand() {
        SuggestionProvider<CommandSourceStack> economySuggestion = (ctx, builder) -> CompletableFuture.supplyAsync(() -> {
            Vault.getInstance().getLegacyEconomyProviders().forEach(ec ->
                    builder.suggest(ec.getProvider().getName())
            );
            Vault.getInstance().getModernEconomyProviders().forEach(ec ->
                    builder.suggest(ec.getProvider().getName())
            );
            return builder.build();
        });


        return Commands.literal("safe-convert")
                .requires(s -> s.getSender().hasPermission("vault.admin"))
                .then(Commands.argument("source", StringArgumentType.word())
                        .suggests(economySuggestion)
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(economySuggestion)
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    String econ1Name = ctx.getArgument("source", String.class);
                                    String econ2Name = ctx.getArgument("target", String.class);

                                    Economy econ1 = null;
                                    Economy econ2 = null;
                                    net.milkbowl.vault2.economy.Economy econ1Unlocked = null;
                                    net.milkbowl.vault2.economy.Economy econ2Unlocked = null;

                                    final StringBuilder economies = new StringBuilder();
                                    for (RegisteredServiceProvider<Economy> e : Vault.getInstance().getLegacyEconomyProviders()) {
                                        String economyName = e.getProvider().getName();
                                        if (economyName.equalsIgnoreCase(econ1Name)) {
                                            econ1 = e.getProvider();
                                        } else if (economyName.equalsIgnoreCase(econ2Name)) {
                                            econ2 = e.getProvider();
                                        }

                                        if (!economies.isEmpty()) {
                                            economies.append(", ");
                                        }
                                        economies.append(economyName);
                                    }
                                    for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> e : Vault.getInstance().getModernEconomyProviders()) {
                                        String economyName = e.getProvider().getName();
                                        if (economyName.equalsIgnoreCase(econ1Name)) {
                                            econ1Unlocked = e.getProvider();
                                        } else if (economyName.equalsIgnoreCase(econ2Name)) {
                                            econ2Unlocked = e.getProvider();
                                        }

                                        if (!economies.isEmpty()) {
                                            economies.append(", ");
                                        }
                                        economies.append(economyName);
                                    }

                                    if (econ1 == null && econ1Unlocked == null) {
                                        sender.sendMessage("Could not find " + econ1Name + " loaded on the server, check your spelling.");
                                        sender.sendMessage("Valid economies are: " + economies);
                                        return Command.SINGLE_SUCCESS;
                                    } else if (econ2 == null && econ2Unlocked == null) {
                                        sender.sendMessage("Could not find " + econ2Name + " loaded on the server, check your spelling.");
                                        sender.sendMessage("Valid economies are: " + economies);
                                        return Command.SINGLE_SUCCESS;
                                    }


                                    sender.sendMessage("This may take some time to convert, expect server lag.");
                                    final boolean useUnlocked1 = (econ1Unlocked != null);
                                    final boolean useUnlocked2 = (econ2Unlocked != null);
                                    final String pluginID = "vault conversion";

                                    for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
                                        if(useUnlocked1) {
                                            if(useUnlocked2) {
                                                if(econ2Unlocked.hasAccount(op.getUniqueId())) {
                                                    continue;
                                                }
                                                econ2Unlocked.createAccount(op.getUniqueId(), op.getName());
                                                final BigDecimal diff = econ1Unlocked.getBalance(pluginID, op.getUniqueId()).subtract(econ2Unlocked.getBalance(pluginID, op.getUniqueId()));
                                                if(diff.compareTo(BigDecimal.ZERO) > 0) {
                                                    econ2Unlocked.deposit(pluginID, op.getUniqueId(), diff);
                                                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                                                    econ2Unlocked.withdraw(pluginID, op.getUniqueId(), diff.negate());
                                                }
                                            } else {
                                                if(econ2.hasAccount(op)) {
                                                    continue;
                                                }
                                                econ2.createPlayerAccount(op);
                                                final BigDecimal diff = econ1Unlocked.getBalance(pluginID, op.getUniqueId()).subtract(BigDecimal.valueOf(econ2.getBalance(op)));
                                                if(diff.compareTo(BigDecimal.ZERO) > 0) {
                                                    econ2.depositPlayer(op, diff.doubleValue());
                                                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                                                    econ2.withdrawPlayer(op, diff.negate().doubleValue());
                                                }
                                            }
                                        } else {
                                            if(useUnlocked2) {
                                                if(econ2Unlocked.hasAccount(op.getUniqueId())) {
                                                    continue;
                                                }
                                                econ2Unlocked.createAccount(op.getUniqueId(), op.getName());
                                                final BigDecimal diff = BigDecimal.valueOf(econ1.getBalance(op)).subtract(econ2Unlocked.getBalance(pluginID, op.getUniqueId()));
                                                if(diff.compareTo(BigDecimal.ZERO) > 0) {
                                                    econ2Unlocked.deposit(pluginID, op.getUniqueId(), diff);
                                                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                                                    econ2Unlocked.withdraw(pluginID, op.getUniqueId(), diff.negate());
                                                }
                                            } else {
                                                if(econ2.hasAccount(op)) {
                                                    continue;
                                                }
                                                econ2.createPlayerAccount(op);
                                                final BigDecimal diff = BigDecimal.valueOf(econ1.getBalance(op)).subtract(BigDecimal.valueOf(econ2.getBalance(op)));
                                                if(diff.compareTo(BigDecimal.ZERO) > 0) {
                                                    econ2.depositPlayer(op, diff.doubleValue());
                                                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                                                    econ2.withdrawPlayer(op, diff.negate().doubleValue());
                                                }
                                            }
                                        }
                                    }
                                    sender.sendMessage("Conversion complete, please verify the data before using it.");


                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

}
