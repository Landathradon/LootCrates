package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static co.neweden.LootCrates.ChestSpawner.*;
import static co.neweden.LootCrates.Database.*;

public class commands implements CommandExecutor {
    private final Plugin plugin;
    public commands(main pl) {
        this.plugin = pl;
    }

    //return all chest count that a player have *member+

    //delete all chests in the world *admin+

    //return amount of chest currently in world *admin+

    //respawn chests *admin+ (delete current + replace new)
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String playermsg = "You must be a player to execute this command!";
        String playerperm = "You do not have the permission to use this";
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("PlayerCrates")) {
                if (args.length == 1) {
                    if (player.hasPermission("lootcrates.player")) {
                        String player_name = args[0];
                        int[] ar = getPlayerLoots(player_name);
                        sender.sendMessage(ChatColor.GOLD + player_name + ChatColor.WHITE + " have a total of " + ChatColor.YELLOW + ar[0] + ChatColor.WHITE + " Crates found\nOne Star: " + ChatColor.YELLOW + ar[1] + ChatColor.WHITE +
                                " | Two Star: " + ChatColor.YELLOW + ar[2] + ChatColor.WHITE + " | Three Star: " + ChatColor.YELLOW + ar[3] + ChatColor.WHITE +
                                " | Four Star: " + ChatColor.YELLOW + ar[4] + ChatColor.WHITE + "\nFive Star: " + ChatColor.YELLOW + ar[5]);
                        return true;
                    } else {
                        sender.sendMessage(playerperm);
                        return false;
                    }
                }
            }
            else if (cmd.getName().equalsIgnoreCase("DeleteCrates")) {
                if (player.hasPermission("lootcrates.delete")) {
                    Bukkit.getScheduler().cancelAllTasks();
                    deleteChest();
                    sender.sendMessage(ChatColor.GREEN + "All Chests have been" + ChatColor.RED + "deleted");
                    return true;
                } else {
                    sender.sendMessage(playerperm);
                    return false;
                }

            }
            else if (cmd.getName().equalsIgnoreCase("CurrentCrates")) {
                if (player.hasPermission("lootcrates.current")) {
                    int val = getCurrentChestsCount();
                    sender.sendMessage("There is currently " + ChatColor.YELLOW + val + ChatColor.WHITE + " crates in the world");
                    return true;
                } else {
                    sender.sendMessage(playerperm);
                    return false;
                }
            }
            else if (cmd.getName().equalsIgnoreCase("RespawnCrates")) {
                if (player.hasPermission("lootcrates.respawn")) {
                    Bukkit.getScheduler().cancelAllTasks();
                    //need to check if crates exists in db
                    int val = getCurrentChestsCount();
                    Crates = 1;
                    if (val != 0) {
                        deleteChest();
                    }
                    CreateChestOnStartup();
                    sender.sendMessage(ChatColor.GREEN + "Every Crates have respawned");
                    return true;
                } else {
                    sender.sendMessage(playerperm);
                    return false;
                }
            }
        } else {
            sender.sendMessage(playermsg);
            return false;
        }
        return false;
    }
}

