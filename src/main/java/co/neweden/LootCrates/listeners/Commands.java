package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.ChestSpawner;
import co.neweden.LootCrates.Database;
import co.neweden.LootCrates.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    deleteCratesCom(sender, args);
                    break;
                case "current":
                case "cur":
                    currentCratesCom(sender, args);
                    break;
                case "respawn":
                    respawnCratesCom(sender, args);
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
                String playerStatsTitle = "--------- " + ChatColor.GOLD + target.name + "'s Stats ";
                Database.Loots loots = Database.getPlayerLoots(null, target.name,"name");
                sender.sendMessage(ChatColor.GRAY + playerStatsTitle + ChatColor.GRAY + checkSpaceLeft(playerStatsTitle) + ChatColor.WHITE +
                "\nA total of " + ChatColor.YELLOW + loots.total + ChatColor.WHITE + " Crates were found by this player" +
                "\nOne Star: " + ChatColor.YELLOW + loots.one_star + ChatColor.WHITE + " | Two Star: " + ChatColor.YELLOW + loots.two_star + ChatColor.WHITE + " | Three Star: " + ChatColor.YELLOW + loots.three_star + ChatColor.WHITE +
                "\nFour Star: " + ChatColor.YELLOW + loots.four_star + ChatColor.WHITE + " | Five Star: " + ChatColor.YELLOW + loots.five_star +
                ChatColor.GRAY + "\n----------------------------------------------");
            } else {
                sender.sendMessage(player_perm);
            }
        }
    }

    private Object checkSpaceLeft(String playerStatsTitle) {
        int chatWidth = 49;
        int titleLength = playerStatsTitle.length();
        int lengthLeft = chatWidth - titleLength;
        StringBuilder textReturn = new StringBuilder();

        while(lengthLeft >= 0){
            textReturn.append("-");
            lengthLeft--;
        }
        return textReturn.toString();
    }

    private void deleteCratesCom(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lootcrates.delete")) {
            sender.sendMessage(player_perm);
            return;
        }
        int realCount = Database.getCurrentChestsCount();
        String msg = ChatColor.GREEN + " crates have been" + ChatColor.RED + " deleted in world: ";
        if (args.length == 2) {
            Database.deleteChest(Bukkit.getWorld(args[1]));
            msg = msg + args[1];
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must enter a world ex: /lc del world");
            } else {
                Player player = (Player) sender;
                msg = msg + player.getWorld().getName();
                Database.deleteChest(player.getWorld());
            }
        }
        if (realCount > 0) {
            sender.sendMessage(ChatColor.YELLOW + String.valueOf(realCount) + msg);
        } else if (realCount == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No" + msg);
        }
    }


    private void currentCratesCom(CommandSender sender, String[] args) {
        if (sender.hasPermission("lootcrates.current")) {
            int realCount = Database.getCurrentChestsCount();
            if (args.length == 2) {
                sender.sendMessage( ChatColor.GRAY + "There is currently " + ChatColor.YELLOW + realCount + ChatColor.GRAY + " crates in the world: " + args[1]);
            } else {
                if (!(sender instanceof Player)){sender.sendMessage("You must enter a world ex: /lc cur world");} else {
                    Player player = (Player) sender;
                    sender.sendMessage(ChatColor.GRAY + "There is currently " + ChatColor.YELLOW + realCount + ChatColor.GRAY + " crates in the world: " + player.getWorld().getName());
                }
            }
        } else {
            sender.sendMessage(player_perm);
        }
    }

    private void respawnCratesCom(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lootcrates.respawn")) {
            sender.sendMessage(player_perm);
            return;
        }
        int realCount = Database.getCurrentChestsCount();
        if (args.length == 2) {
            if (realCount > 0) {
                Database.deleteChest(Bukkit.getWorld(args[1]));
                ChestSpawner.CreateChestOnStartup(Bukkit.getWorld(args[1]));
            } else if (realCount == 0) {
                ChestSpawner.CreateChestOnStartup(Bukkit.getWorld(args[1]));
            }
            sender.sendMessage(ChatColor.GREEN + "Every crates have respawned in: " + args[1]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must enter a world ex: /lc respawn world");
            } else {
                Player player = (Player) sender;

                if (realCount > 0) {
                    Database.deleteChest(player.getWorld());
                    ChestSpawner.CreateChestOnStartup(player.getWorld());
                } else if (realCount == 0) {
                    ChestSpawner.CreateChestOnStartup(player.getWorld());
                }
                sender.sendMessage(ChatColor.GREEN + "Every crates have respawned in: " + player.getWorld().getName());
            }
        }
    }

    private void reloadCom(CommandSender sender) {
        if (sender.hasPermission("lootcrates.reload")) {
            plugin.onDisable();
            plugin.reloadConfig();
            plugin.onEnable();
            sender.sendMessage(ChatColor.GREEN + "[LootCrates] Config reloaded!");
        }
    }

    private void lcCom(CommandSender sender) {

        sender.sendMessage(ChatColor.YELLOW + "Usage:\n" +
                "/lootcrates player [player] | This show user crates stats\n" +
                "/lootcrates delete | This delete all current spawned crates\n" +
                "/lootcrates current | This show how many crates are spawned\n" +
                "/lootcrates respawn | This will respawn crates up to max amount\n" +
                "/lootcrates reload | This will reload the plugin's config");
    }

    private class Target{
        String name;
    }
}
