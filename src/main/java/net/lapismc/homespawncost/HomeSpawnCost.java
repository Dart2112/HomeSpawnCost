package net.lapismc.homespawncost;

import net.lapismc.homespawn.api.events.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class HomeSpawnCost extends JavaPlugin implements Listener, CommandExecutor
{

    private static Economy econ;
    private String currencySymbol;
    private Logger logger = getLogger();

    @Override
    public void onEnable() 
    {
        configs();
        if (!setupEconomy()) 
        {
            logger.severe("We can't find Vault. Install it. Disabling!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        currencySymbol = getConfig().getString("messages.currency_symbol");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private boolean setupEconomy() 
    {
        if (getServer().getPluginManager().getPlugin("Vault") == null) 
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) 
        {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void configs() 
    {
        File config = new File(getDataFolder(), "config.yml");
        if (config.exists()) 
        {
            if (getConfig().getInt("config_version") != 2) 
            {
                File configBackup = new File(getDataFolder(), "config_backup.yml");
                config.renameTo(configBackup);
                logger.info("Config has been updated, please transfer values.");
                saveDefaultConfig();
            }
            else
            {
            	saveDefaultConfig();
            }
        }
        else 
        {
            saveDefaultConfig();
        }
    }

    private String getMessage(String key) 
    {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
    	if(cmd.getName().equalsIgnoreCase("hsc-reload"))
        {
            if (args.length > 0)
            {
                sender.sendMessage(getMessage("messages.reload_many_arg"));
                return false;
            }
            else
            {
                getPluginLoader().disablePlugin(this);
                getPluginLoader().enablePlugin(this);
                saveDefaultConfig();
                reloadConfig();
                sender.sendMessage(getMessage("messages.reload"));
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onHomeMove(HomeMoveEvent e) 
    {
        if (getConfig().getBoolean("use.moving_home")) 
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.move_home");
            if (balance < cost) 
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("messages.move_home").replace("$cost$", currencySymbol + cost).replace("$name$", e.getName()));
                }
            }
        }
    }

    @EventHandler
    public void onHomeSet(HomeSetEvent e) 
    {
        if (getConfig().getBoolean("use.setting_home"))
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.set_home");
            if (balance < cost)
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess())
                {
                    e.getPlayer().sendMessage(getMessage("messages.set_home").replace("$cost$", currencySymbol + cost).replace("$home$", e.getHome().getName()));
                }
            }
        }
    }

    @EventHandler
    public void onHomeDelete(HomeDeleteEvent e)
    {
        if (getConfig().getBoolean("use.deleting_home"))
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.deleting_home");
            if (balance < cost)
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess())
                {
                    e.getPlayer().sendMessage(getMessage("messages.delete_home").replace("$cost$", currencySymbol + cost).replace("$home$", e.getHome().getName()));
                }
            }
        }
    }

    @EventHandler
    public void onHomeRename(HomeRenameEvent e) 
    {
        if (getConfig().getBoolean("use.renaming_home")) 
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.rename_home");
            if (balance < cost) 
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess())
                {
                    e.getPlayer().sendMessage(getMessage("messages.rename_home").replace("$cost$", currencySymbol + cost).replace("$old_name$", e.getOldHome()).replace("$new_name$", e.getNewHome()));
                }
            }
        }
    }

    @EventHandler
    public void onHomeTeleport(HomeTeleportEvent e)
    {
        if (getConfig().getBoolean("use.teleporting_home"))
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.teleport_home");
            if (balance < cost)
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess())
                {
                    e.getPlayer().sendMessage(getMessage("messages.teleport_home").replace("$cost$", currencySymbol + cost).replace("$name$", e.getHome().getName()));
                }
            }
        }
    }
    @EventHandler
    public void onSpawnTeleport(SpawnTeleportEvent e)
    {
        if (getConfig().getBoolean("use.teleporting_spawn"))
        {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("cost.teleport_spawn");
            if (balance < cost)
            {
                e.setCancelled(true, getMessage("messages.not_enough_money").replace("$cost$", currencySymbol + cost).replace("$balance$", currencySymbol + balance));
            }
            else
            {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess())
                {
                    e.getPlayer().sendMessage(getMessage("messages.teleport_spawn").replace("$cost$", currencySymbol + cost));
                }
            }
        }
    }
}
