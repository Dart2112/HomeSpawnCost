package net.lapismc.homespawncost;

import net.lapismc.HomeSpawn.util.core.LapisCoreConfiguration;
import net.lapismc.HomeSpawn.util.core.LapisCorePlugin;
import net.lapismc.homespawn.api.events.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class HomeSpawnCost extends LapisCorePlugin implements Listener {

    private static Economy econ;
    private String currencySymbol;

    @Override
    public void onEnable() {
        registerConfiguration(new LapisCoreConfiguration(this, 2, 1));
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        currencySymbol = getConfig().getString("messages.currency_symbol");
        Bukkit.getPluginManager().registerEvents(this, this);
        new ReloadCommand(this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    /*
    EVENTS
     */

    @EventHandler
    public void onHomeMove(HomeMoveEvent e) {
        if (getConfig().getBoolean("use.moving_home")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = e.getName().equals("Home") ? getConfig().getInt("cost.move_home") : getConfig().getInt("cost.move_home");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("move_home").replace("%cost%", currencySymbol + cost).replace("%name%", e.getName()));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }

    @EventHandler
    public void onHomeSet(HomeSetEvent e) {
        if (getConfig().getBoolean("use.setting_home")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = e.getHome().getName().equals("Home") ? getConfig().getInt("cost.set_home") : getConfig().getInt("cost.set_home");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("set_home").replace("%cost%", currencySymbol + cost).replace("%home%", e.getHome().getName()));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }

    @EventHandler
    public void onHomeDelete(HomeDeleteEvent e) {
        if (getConfig().getBoolean("use.deleting_home")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = e.getHome().getName().equals("Home") ? getConfig().getInt("cost.delete_home") : getConfig().getInt("cost.delete_home");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("delete_home").replace("%cost%", currencySymbol + cost).replace("%home%", e.getHome().getName()));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }

    @EventHandler
    public void onHomeRename(HomeRenameEvent e) {
        if (getConfig().getBoolean("use.renaming_home")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = getConfig().getInt("cost.rename_home");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("rename_home").replace("%cost%", currencySymbol + cost).replace("%old_name%", e.getOldHome()).replace("%new_name%", e.getNewHome()));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }

    @EventHandler
    public void onHomeTeleport(HomeTeleportEvent e) {
        if (getConfig().getBoolean("use.teleporting_home")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = e.getHome().getName().equals("Home") ? getConfig().getInt("cost.teleport_home") : getConfig().getInt("cost.teleport_home");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("teleport_home").replace("%cost%", currencySymbol + cost).replace("%name%", e.getHome().getName()));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }

    @EventHandler
    public void onSpawnTeleport(SpawnTeleportEvent e) {
        if (getConfig().getBoolean("use.teleporting_spawn")) {
            double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            int cost = getConfig().getInt("cost.teleport_spawn");
            if (balance >= cost && econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                e.getPlayer().sendMessage(config.getMessage("teleport_spawn").replace("%cost%", currencySymbol + cost));
            } else {
                e.setCancelled(true, config.getMessage("not_enough_money").replace("%cost%", currencySymbol + cost).replace("%balance%", currencySymbol + balance));
            }
        }
    }
}
