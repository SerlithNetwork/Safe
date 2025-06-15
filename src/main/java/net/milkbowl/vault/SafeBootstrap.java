package net.milkbowl.vault;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.command.SafeCommandLoader;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class SafeBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(SafeCommandLoader.buildInfoCommand(), "Displays information about Safe");
            commands.registrar().register(SafeCommandLoader.buildConvertCommand(), "Converts all economy data in <source> and dumps it into <target>");
        });
    }

}
