package net.milkbowl.vault.listener;

import net.milkbowl.vault.types.VersionInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VaultListener implements Listener {

    private static final String VAULT_BUKKIT_URL = "https://github.com/SerlithNetwork/Safe";

    private final VersionInfo versionInfo;

    public VaultListener(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("vault.update")) {
            try {
                if (this.versionInfo.getNewVersion() > this.versionInfo.getCurrentVersion()) {
                    player.sendMessage("Vault " +  this.versionInfo.getNewVersionTitle() + " is out! You are running " + this.versionInfo.getCurrentVersionTitle());
                    player.sendMessage("Update Vault at: " + VAULT_BUKKIT_URL);
                }
            } catch (Exception ignore) {}
        }
    }

}
