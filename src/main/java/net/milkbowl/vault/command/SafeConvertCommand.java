package net.milkbowl.vault.command;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SafeConvertCommand extends Command implements PluginIdentifiableCommand {

    private final @NotNull Vault plugin;

    public SafeConvertCommand(@NotNull Vault plugin) {
        super("safe-convert");
        this.plugin = plugin;

        this.setPermission("vault.admin");
        this.setUsage("/safe-convert [economy1] [economy2]");
        this.setDescription("Converts all data in economy1 and dumps it into economy2");
        this.setAliases(Collections.singletonList("vault-convert"));

        this.plugin.getServer().getCommandMap().register("safe", this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        final Collection<RegisteredServiceProvider<Economy>> econs = this.plugin.getServer().getServicesManager().getRegistrations(Economy.class);
        final Collection<RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>> econs2 = this.plugin.getServer().getServicesManager().getRegistrations(net.milkbowl.vault2.economy.Economy.class);

        if (econs == null || econs.size() < 2 && econs2.isEmpty()) {
            sender.sendMessage("You must have at least 2 economies loaded to convert.");
            return false;
        } else if (args.length != 2) {
            sender.sendMessage("You must specify only the economy to convert from and the economy to convert to. (names should not contain spaces)");
            return false;
        }

        Economy econ1 = null;
        Economy econ2 = null;
        net.milkbowl.vault2.economy.Economy econ1Unlocked = null;
        net.milkbowl.vault2.economy.Economy econ2Unlocked = null;

        final StringBuilder economies = new StringBuilder();
        for (RegisteredServiceProvider<Economy> econ : econs) {
            String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {
                econ1 = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {
                econ2 = econ.getProvider();
            }
            if (!economies.isEmpty()) {
                economies.append(", ");
            }
            economies.append(econName);
        }
        for (RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> econ : econs2) {
            final String econName = econ.getProvider().getName().replace(" ", "");
            if (econName.equalsIgnoreCase(args[0])) {
                econ1Unlocked = econ.getProvider();
            } else if (econName.equalsIgnoreCase(args[1])) {
                econ2Unlocked = econ.getProvider();
            }

            if (!economies.isEmpty()) {
                economies.append(", ");
            }
            economies.append(econName);
        }

        if (econ1 == null && econ1Unlocked == null) {
            sender.sendMessage("Could not find " + args[0] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return false;
        } else if (econ2 == null && econ2Unlocked == null) {
            sender.sendMessage("Could not find " + args[1] + " loaded on the server, check your spelling.");
            sender.sendMessage("Valid economies are: " + economies);
            return false;
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
        return true;
    }

    private final List<String> empty = Collections.emptyList();
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1 || args.length > 2) return this.empty;
        Collection<RegisteredServiceProvider<Economy>> econs = this.plugin.getServer().getServicesManager().getRegistrations(Economy.class);
        return econs.stream().map(e -> e.getProvider().getName().replace(" ", "")).toList();
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

}
