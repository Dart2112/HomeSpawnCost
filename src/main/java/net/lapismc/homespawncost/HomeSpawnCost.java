package net.lapismc.homespawncost;

import net.lapismc.homespawn.api.events.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class HomeSpawnCost extends JavaPlugin implements Listener {

    private static Economy econ;
    private String currencySymbol;
    private Logger logger = getLogger();

    @Override
    public void onEnable() {
        configs();
        if (!setupEconomy()) {
            logger.severe("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        currencySymbol = getConfig().getString("Messages.CurrencySymbol");
        Bukkit.getPluginManager().registerEvents(this, this);
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
        return econ != null;
    }

    private void configs() {
        File config = new File(getDataFolder(), "config.yml");
        if (config.exists()) {
            if (getConfig().getInt("ConfigVersion") != 1) {
                File configBackup = new File(getDataFolder(), "config_backup.yml");
                config.renameTo(configBackup);
                logger.info("Config has been updated, Please transfer values");
                saveDefaultConfig();
            }
        } else {
            saveDefaultConfig();
        }
    }

    private String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(key));
    }

    @EventHandler
    public void onHomeMove(HomeMoveEvent e) {
        if (getConfig().getBoolean("Charging.Moving")) {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = e.getName().equals("Home") ? getConfig().getInt("Costs.MoveMainHome") : getConfig().getInt("Costs.MoveCustomHome");
            if (balance < cost) {
                e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
            } else {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("Messages.MoneySpent").replace("%COST%", currencySymbol + cost).replace("%ACTION%", "move your home"));
                } else {
                    e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
                }
            }
        }
    }

    @EventHandler
    public void onHomeSet(HomeSetEvent e) {
        if (getConfig().getBoolean("Charging.Setting")) {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = e.getHome().getName().equals("Home") ? getConfig().getInt("Costs.SetMainHome") : getConfig().getInt("Costs.SetCustomHome");
            if (balance < cost) {
                e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
            } else {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("Messages.MoneySpent").replace("%COST%", currencySymbol + cost).replace("%ACTION%", "set your home"));
                } else {
                    e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
                }
            }
        }
    }

    @EventHandler
    public void onHomeDelete(HomeDeleteEvent e) {
        if (getConfig().getBoolean("Charging.Deleting")) {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = e.getHome().getName().equals("Home") ? getConfig().getInt("Costs.DeleteMainHome") : getConfig().getInt("Costs.DeleteCustomHome");
            if (balance < cost) {
                e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
            } else {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("Messages.MoneySpent").replace("%COST%", currencySymbol + cost).replace("%ACTION%", "delete your home"));
                } else {
                    e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
                }
            }
        }
    }

    @EventHandler
    public void onHomeRename(HomeRenameEvent e) {
        if (getConfig().getBoolean("Charging.Deleting")) {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = getConfig().getInt("Costs.RenameHome");
            if (balance < cost) {
                e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
            } else {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("Messages.MoneySpent").replace("%COST%", currencySymbol + cost).replace("%ACTION%", "rename your home"));
                } else {
                    e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
                }
            }
        }
    }

    @EventHandler
    public void onHomeTeleport(HomeTeleportEvent e) {
        if (getConfig().getBoolean("Charging.Teleporting")) {
            Double balance = econ.getBalance(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
            Integer cost = e.getHome().getName().equals("Home") ? getConfig().getInt("Costs.TeleportMainHome") : getConfig().getInt("Costs.TeleportCustomHome");
            if (balance < cost) {
                e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
            } else {
                if (econ.withdrawPlayer(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()), cost).transactionSuccess()) {
                    e.getPlayer().sendMessage(getMessage("Messages.MoneySpent").replace("%COST%", currencySymbol + cost).replace("%ACTION%", "teleport to your home"));
                } else {
                    e.setCancelled(true, getMessage("Messages.NotEnoughMoney").replace("%COST%", currencySymbol + cost).replace("%Balance%", currencySymbol + balance));
                }
            }
        }
    }
}
