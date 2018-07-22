package co.neweden.LootCrates.listeners;

import co.neweden.LootCrates.ChestSpawner;
import co.neweden.LootCrates.ConfigRetriever;
import co.neweden.LootCrates.Database;
import co.neweden.LootCrates.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getWorld;

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
                case "hide":
                    hideCom(sender);
                    break;
                default:
                    lcCom(sender);
                    break;
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    private void hideCom(CommandSender sender) {
        Database.updateHidePlayerMsg((Player) sender);
        sender.sendMessage(ConfigRetriever.lootcratesPrefix + "Your message setting has been changed");
    }

    private void playerCratesCom(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (sender.hasPermission("lootcrates.player")) {
                Target target = new Target();
                target.name = args[1].toLowerCase();
                String playerStatsTitle = "--------- " + ChatColor.GOLD + target.name + "'s Stats ";
                Database.Loots loots = Database.getPlayerData(null, target.name,"name");
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
        int realCount = 0;
        String msg = ChatColor.GREEN + " crates have been" + ChatColor.RED + " deleted in world: " + ChatColor.YELLOW;
        if (args.length == 2) {
            realCount = Database.getCurrentChestsCount(getWorld(args[1]));
            Database.deleteChest(getWorld(args[1]));
            msg = msg + args[1];
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must enter a world ex: /lc del world");
            } else {
                Player player = (Player) sender;
                realCount = Database.getCurrentChestsCount(player.getWorld());
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
            int realCount;
            if (args.length == 2) {
                realCount = Database.getCurrentChestsCount(getWorld(args[1]));
                sender.sendMessage( ChatColor.GRAY + "There is currently " + ChatColor.YELLOW + realCount + ChatColor.GRAY + " crates in the world: " + ChatColor.YELLOW + args[1]);
            } else {
                if (!(sender instanceof Player)){sender.sendMessage("You must enter a world ex: /lc cur world");} else {
                    Player player = (Player) sender;
                    realCount = Database.getCurrentChestsCount(player.getWorld());
                    sender.sendMessage(ChatColor.GRAY + "There is currently " + ChatColor.YELLOW + realCount + ChatColor.GRAY + " crates in the world: " + ChatColor.YELLOW + player.getWorld().getName());
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
        int realCount;
        if (args.length == 2) {
            realCount = Database.getCurrentChestsCount(getWorld(args[1]));
            if (realCount > 0) {
                Database.deleteChest(getWorld(args[1]));
                ChestSpawner.CreateChestOnStartup(getWorld(args[1]));
            } else if (realCount == 0) {
                ChestSpawner.CreateChestOnStartup(getWorld(args[1]));
            }
            sender.sendMessage(ChatColor.GREEN + "Every crates have respawned in: " + ChatColor.YELLOW + args[1]);
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must enter a world ex: /lc respawn world");
            } else {
                Player player = (Player) sender;
                realCount = Database.getCurrentChestsCount(player.getWorld());

                if (realCount > 0) {
                    Database.deleteChest(player.getWorld());
                    ChestSpawner.CreateChestOnStartup(player.getWorld());
                } else if (realCount == 0) {
                    ChestSpawner.CreateChestOnStartup(player.getWorld());
                }
                sender.sendMessage(ChatColor.GREEN + "Every crates have respawned in: " + ChatColor.YELLOW + player.getWorld().getName());
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
                "/lootcrates hide | This will hide future messages");
        if(sender.hasPermission("lootcrates.delete"))
            sender.sendMessage(ChatColor.YELLOW + "/lootcrates delete | This delete all current spawned crates");
        if(sender.hasPermission("lootcrates.current"))
            sender.sendMessage(ChatColor.YELLOW + "/lootcrates current | This show how many crates are spawned");
        if(sender.hasPermission("lootcrates.respawn"))
            sender.sendMessage(ChatColor.YELLOW + "/lootcrates respawn | This will respawn crates up to max amount");
        if(sender.hasPermission("lootcrates.reload"))
            sender.sendMessage(ChatColor.YELLOW + "/lootcrates reload | This will reload the plugin's config");
    }

    private class Target{
        String name;
    }
}
