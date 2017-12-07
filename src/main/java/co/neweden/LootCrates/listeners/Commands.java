package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static co.neweden.LootCrates.ChestSpawner.*;
import static co.neweden.LootCrates.ConfigRetriever.checkConfig;
import static co.neweden.LootCrates.ConfigRetriever.getConfigStuff;
import static co.neweden.LootCrates.Database.*;

public class Commands implements CommandExecutor {
    private static Main plugin;

    public Commands(Main pl) {
        plugin = pl;
    }

    private String player_perm = "You do not have the permission to use this";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("LootCrates")){
            if (args.length > 0) {
                subCommand(sender, args);
                return true;
            }else{
                lcCom(sender);
                return true;
            }
        }
        return false;
    }

    private void subCommand(CommandSender sender, String[] args) {

        try {

            switch (args[0].toLowerCase()) {
                case "player":
                case "p":
                    playerCratesCom(sender, args);
                    break;
                case "delete":
                case "del":
                    deleteCratesCom(sender);
                    break;
                case "current":
                case "cur":
                    currentCratesCom(sender);
                    break;
                case "respawn":
                    respawnCratesCom(sender);
                    break;
                case "reload":
                case "rl":
                    reloadCom(sender);
                    break;
                default:
                    lcCom(sender);
                    break;
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    private void playerCratesCom(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (sender.hasPermission("lootcrates.player")) {
                Target target = new Target();
                target.name = args[1].toLowerCase();
                target.uuid = getPlayerUUID(target.name);
                Loots loots = getPlayerLoots(target.uuid);
                sender.sendMessage(ChatColor.GOLD + target.name + ChatColor.WHITE + " have a total of " + ChatColor.YELLOW + loots.total + ChatColor.WHITE + " Crates found" +
                "\nOne Star: " + ChatColor.YELLOW + loots.one_star + ChatColor.WHITE + " | Two Star: " + ChatColor.YELLOW + loots.two_star + ChatColor.WHITE + " | Three Star: " + ChatColor.YELLOW + loots.three_star + ChatColor.WHITE +
                "\nFour Star: " + ChatColor.YELLOW + loots.four_star + ChatColor.WHITE + " | Five Star: " + ChatColor.YELLOW + loots.five_star);
            } else {
                sender.sendMessage(player_perm);
            }
        }
    }

    private void deleteCratesCom(CommandSender sender) {
        if (sender.hasPermission("lootcrates.delete")) {
            Bukkit.getScheduler().cancelAllTasks();
            count = 1;
            deleteChest();
            int realCount = count - 1;
            String msg = ChatColor.GREEN + " crates have been" + ChatColor.RED + " deleted";
            if(realCount > 0) {
                sender.sendMessage(ChatColor.YELLOW + String.valueOf(realCount) + msg);
            }else if(realCount == 0){
                sender.sendMessage(ChatColor.YELLOW + "No" + msg);
            }
        } else {
            sender.sendMessage(player_perm);
        }
    }

    private void currentCratesCom(CommandSender sender) {
        if (sender.hasPermission("lootcrates.current")) {
            sender.sendMessage( ChatColor.GRAY + "There is currently " + ChatColor.YELLOW + getCurrentChestsCount() + ChatColor.GRAY + " crates in the world");
        } else {
            sender.sendMessage(player_perm);
        }
    }

    private void respawnCratesCom(CommandSender sender) {
        if (sender.hasPermission("lootcrates.respawn")) {
            Bukkit.getScheduler().cancelAllTasks();
            Crates = 1;
            if (getCurrentChestsCount() > 0) {
                count = 1;
                deleteChest();
                CreateChestOnStartup();
            } else if (getCurrentChestsCount() == 0) {
                CreateChestOnStartup();
            }
            sender.sendMessage(ChatColor.GREEN + "Every crates have respawn");
        } else {
            sender.sendMessage(player_perm);
        }
    }

    private void reloadCom(CommandSender sender) {
        if (sender.hasPermission("lootcrates.reload")) {
            plugin.reloadConfig();
            getConfigStuff();
            checkConfig(1);
            sender.sendMessage(ChatColor.GREEN + "[LootCrates] Config reloaded!");
        }
    }

    private void lcCom(CommandSender sender) {

        sender.sendMessage(ChatColor.YELLOW + "Usage:\n /lootcrates player [player]\n" +
                "/lootcrates delete\n" +
                "/lootcrates current\n" +
                "/lootcrates respawn\n" +
                "/lootcrates reload");
    }

    //Deprecated, might need to change soon
    private UUID getPlayerUUID(String name){
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return op.getUniqueId();
    }

    private class Target{
        String name;
        UUID uuid;
    }
}
