package net.lapismc.homespawncost;

import net.lapismc.HomeSpawn.util.core.commands.LapisCoreCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;

public class ReloadCommand extends LapisCoreCommand {

    HomeSpawnCost core;

    protected ReloadCommand(HomeSpawnCost core) {
        super(core, "hscreload", "Reload HomeSpawnCost", new ArrayList<>(Collections.singletonList("homespawncostreload")));
        this.core = core;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(core.config.getMessage("reload_many_arg"));
        } else {
            core.config.generateConfigs();
            core.reloadConfig();
            core.config.reloadMessages();
            sender.sendMessage(core.config.getMessage("reload"));
        }
    }
}
