package net.milkbowl.vault.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@UtilityClass
@SuppressWarnings("UnstableApiUsage")
public class SafeCommandLoader {

    private static final TextColor COLOR_VALID = TextColor.color(77, 255, 190);
    private static final TextColor COLOR_INVALID = TextColor.color(255, 163, 163);
    private static final TextColor COLOR_COMMA = TextColor.color(173, 173, 173);

    @SuppressWarnings("deprecation")
    public LiteralCommandNode<CommandSourceStack> buildInfoCommand() {
        return Commands.literal("vault-info")
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

                    MiniMessage miniMessage = MiniMessage.builder()
                            .tags(TagResolver.builder()
                                    .resolver(StandardTags.color())
                                    .resolver(Placeholder.unparsed("version", Vault.getInstance().getPluginMeta().getVersion()))
                                    .resolver(Placeholder.unparsed("eln", econ == null ? "None" : econ.getName()))
                                    .resolver(Placeholder.unparsed("emn", econ2 == null ? "None" : econ2.getName()))
                                    .resolver(Placeholder.unparsed("pln", perm == null ? "None" : perm.getName()))
                                    .resolver(Placeholder.unparsed("pmn", perm2 == null ? "None" : perm2.getName()))
                                    .resolver(Placeholder.unparsed("cln", chat == null ? "None" : chat.getName()))
                                    .resolver(Placeholder.unparsed("cmn", chat2 == null ? "None" : chat2.getName()))
                                    .resolver(Placeholder.unparsed("ela", registeredEcons == null ? "None" : registeredEcons.toString()))
                                    .resolver(Placeholder.unparsed("ema", registeredModernEcons.isEmpty() ? "None" : registeredModernEcons.toString()))
                                    .resolver(Placeholder.unparsed("pla", registeredPerms == null ? "None" : registeredPerms.toString()))
                                    .resolver(Placeholder.unparsed("pma", registeredModernPerms.isEmpty() ? "None" : registeredModernPerms.toString()))
                                    .resolver(Placeholder.unparsed("cla", registeredChats == null ? "None" : registeredChats.toString()))
                                    .resolver(Placeholder.unparsed("cma", registeredModernChats.isEmpty() ? "None" : registeredModernChats.toString()))
                                    .build()
                            ).build();

                    // Send user some info!
                    List.of(
                            Vault.getPrefix().append(miniMessage.deserialize("Safe v<version> Information")),
                            Vault.getPrefix().append(miniMessage.deserialize("Economy <#ffa3a3>Legacy</#ffa3a3>: <#4dffbe><eln></#4dffbe> [<#adadad><ela></#adadad>]")),
                            Vault.getPrefix().append(miniMessage.deserialize("Economy <#a3f6ff>Modern</#a3f6ff>: <#4dffbe><emn></#4dffbe> [<#adadad><ema></#adadad>]")),
                            Vault.getPrefix().append(miniMessage.deserialize("Permission <#ffa3a3>Legacy</#ffa3a3>: <#4dffbe><pln></#4dffbe> [<#adadad><pla></#adadad>]")),
                            Vault.getPrefix().append(miniMessage.deserialize("Permission <#a3f6ff>Modern</#a3f6ff>: <#4dffbe><pmn></#4dffbe> [<#adadad><pma></#adadad>]")),
                            Vault.getPrefix().append(miniMessage.deserialize("Chat <#ffa3a3>Legacy</#ffa3a3>: <#4dffbe><cln></#4dffbe> [<#adadad><cla></#adadad>]")),
                            Vault.getPrefix().append(miniMessage.deserialize("Chat <#a3f6ff>Modern</#a3f6ff>: <#4dffbe><cmn></#4dffbe> [<#adadad><cma></#adadad>]"))
                    ).forEach(sender::sendMessage);

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


        return Commands.literal("vault-convert")
                .requires(s -> s.getSender().hasPermission("vault.admin"))
                .then(Commands.argument("source", StringArgumentType.word())
                        .suggests(economySuggestion)
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(economySuggestion)
                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();

                                    String econ1Name = ctx.getArgument("source", String.class);
                                    String econ2Name = ctx.getArgument("target", String.class);

                                    if (econ1Name.equalsIgnoreCase(econ2Name)) {
                                        sender.sendMessage(Vault.getPrefix().append(Component.text("You can't convert a economy into itself")));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    Economy econ1 = null;
                                    Economy econ2 = null;
                                    net.milkbowl.vault2.economy.Economy econ1Unlocked = null;
                                    net.milkbowl.vault2.economy.Economy econ2Unlocked = null;

                                    final List<String> ecos = new ArrayList<>();
                                    final Component empty = Component.empty();
                                    Component economies = empty;
                                    for (RegisteredServiceProvider<Economy> e : Vault.getInstance().getLegacyEconomyProviders()) {
                                        String economyName = e.getProvider().getName();
                                        if (economyName.equalsIgnoreCase(econ1Name)) {
                                            econ1 = e.getProvider();
                                        } else if (economyName.equalsIgnoreCase(econ2Name)) {
                                            econ2 = e.getProvider();
                                        }

                                        if (ecos.contains(economyName)) continue;
                                        if (economies != empty) {
                                            economies = economies.append(Component.text(", ", COLOR_COMMA));
                                        }
                                        economies = economies.append(Component.text(economyName, COLOR_VALID));
                                        ecos.add(economyName);
                                    }
                                    for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> e : Vault.getInstance().getModernEconomyProviders()) {
                                        String economyName = e.getProvider().getName();
                                        if (economyName.equalsIgnoreCase(econ1Name)) {
                                            econ1Unlocked = e.getProvider();
                                        } else if (economyName.equalsIgnoreCase(econ2Name)) {
                                            econ2Unlocked = e.getProvider();
                                        }

                                        if (ecos.contains(economyName)) continue;
                                        if (economies != empty) {
                                            economies = economies.append(Component.text(", ", COLOR_COMMA));
                                        }
                                        economies = economies.append(Component.text(economyName, COLOR_VALID));
                                        ecos.add(economyName);
                                    }

                                    if (econ1 == null && econ1Unlocked == null) {
                                        List.of(
                                                Vault.getPrefix().append(Component.text("Could not find ")).append(Component.text(econ1Name, COLOR_INVALID)).append(Component.text(" loaded on the server, check your spelling.")),
                                                Vault.getPrefix().append(Component.text("Valid economies are: ")).append(economies)
                                        ).forEach(sender::sendMessage);
                                        return Command.SINGLE_SUCCESS;
                                    } else if (econ2 == null && econ2Unlocked == null) {
                                        List.of(
                                                Vault.getPrefix().append(Component.text("Could not find ")).append(Component.text(econ2Name, COLOR_INVALID)).append(Component.text(" loaded on the server, check your spelling.")),
                                                Vault.getPrefix().append(Component.text("Valid economies are: ")).append(economies)
                                        ).forEach(sender::sendMessage);
                                        return Command.SINGLE_SUCCESS;
                                    }


                                    sender.sendMessage(Vault.getPrefix().append(Component.text("This may take some time to convert, expect server lag.")));
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
                                    sender.sendMessage(Vault.getPrefix().append(Component.text("Conversion complete, please verify the data before using it.")));


                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .build();
    }

}
