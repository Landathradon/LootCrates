package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static co.neweden.LootCrates.ChestSpawner.*;
import static co.neweden.LootCrates.Database.*;

public class commands implements CommandExecutor {
    public commands(@SuppressWarnings("unused") main pl) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String player_msg = "You must be a player to execute this command!";
        String player_perm = "You do not have the permission to use this";
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
                        sender.sendMessage(player_perm);
                        return false;
                    }
                }
            }
            else if (cmd.getName().equalsIgnoreCase("DeleteCrates")) {
                if (player.hasPermission("lootcrates.delete")) {
                    Bukkit.getScheduler().cancelAllTasks();
                    count = 1;
                    deleteChest();
                    sender.sendMessage(ChatColor.GREEN + "All " + ChatColor.YELLOW + (count-1) + ChatColor.GREEN + " crates have been" + ChatColor.RED + " deleted");
                    return true;
                } else {
                    sender.sendMessage(player_perm);
                    return false;
                }

            }
            else if (cmd.getName().equalsIgnoreCase("CurrentCrates")) {
                if (player.hasPermission("lootcrates.current")) {
                    sender.sendMessage("There is currently " + ChatColor.YELLOW + getCurrentChestsCount() + ChatColor.WHITE + " crates in the world");
                    return true;
                } else {
                    sender.sendMessage(player_perm);
                    return false;
                }
            }
            else if (cmd.getName().equalsIgnoreCase("RespawnCrates")) {
                if (player.hasPermission("lootcrates.respawn")) {
                    Bukkit.getScheduler().cancelAllTasks();
                    //need to check if crates exists in db
                    Crates = 1;
                    if (getCurrentChestsCount() > 0) {
                        count = 1;
                        deleteChest();
                        CreateChestOnStartup();
                    }
                    else if(getCurrentChestsCount() == 0) {
                        CreateChestOnStartup();
                    }
                    sender.sendMessage(ChatColor.GREEN + "Every crates have respawn");
                    return true;
                } else {
                    sender.sendMessage(player_perm);
                    return false;
                }
            }
        } else {
            sender.sendMessage(player_msg);
            return false;
        }
        return false;
    }
}

