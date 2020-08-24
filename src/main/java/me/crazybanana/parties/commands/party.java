package me.crazybanana.parties.commands;

import me.crazybanana.parties.ExpireTimer;
import me.crazybanana.parties.PartyGroup;
import org.bukkit.Bukkit;
import static org.bukkit.ChatColor.*;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class party implements CommandExecutor {
      public HashMap<String, PartyGroup> parties = new HashMap<String, PartyGroup>();
      private int expireTime = 60;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            // Checks
            if (args == null || args.length <= 0 || args.length > 2) {
                sender.sendMessage(ChatColor.AQUA + "Hey" + ChatColor.RED + "!" + ChatColor.AQUA + " Use " + ChatColor.RED + "/party help" + ChatColor.AQUA + " to see a list of the commands!");
                return false;
            }
            if (!(sender instanceof Player)) {
                return false;
            }

            // Setting Sender
            Player player = (Player) sender;


            // Accepting Code
            if (args[0].equalsIgnoreCase("accept")) {
                acceptHandler(player);
            }
            // Leave Code
            else if (args[0].equalsIgnoreCase("leave")) {
                leaveHandler(player);
            }
            // List Code
            else if (args[0].equalsIgnoreCase("list")) {
                listHandler(player);
            }
            // Help Code
            else if (args[0].equalsIgnoreCase("help")) {
                helpHandler(player);
            }
            // Kicked Code
            else if (args[0].equalsIgnoreCase("kick")) {
                if(args.length < 2) {
                    player.sendMessage(RED + "do /party help");
                    return false;
                }
                kickHandler(player.getName(), args[1]);
            }
            // Debug Code
            else if (args[0].equalsIgnoreCase("debug")) {
                debugHandler(player);
            }
            // Send Code
            else {
             //   player.performCommand("warp warptest");
             //   Bukkit.getServer().getPlayer(args[0]).performCommand("warp warptest");
             //   Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "warp warptest " + player.getName());
             //   Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "warp warptest " + args[0]);
                sendHandler(player, args);
            }
        }
        catch(Exception ex) {
            sender.sendMessage(ChatColor.RED + "Error :(");
        }

        return true;
    }




    private void sendHandler(Player player, String[] args) {
        // Temp Var.
        String leader = player.getName();
        // NOT A LEADER (IN A PARTY) TRYING TO INVITE OTHERS
        boolean checkf = isInOtherParty(player.getName(), false);
        if (checkf) {
            player.sendMessage(ChatColor.AQUA + "You are already " + ChatColor.RED + "in another party!");
            return;
        }

        // Code
        if(!parties.containsKey(leader)) {
            parties.put(leader, new PartyGroup(leader));
        }


        for(int i=0;i<args.length;i++) {
            try {
                PartyGroup group = parties.get(leader);
                boolean check = isInOtherParty(args[i], true);
                if (check) {
                    player.sendMessage(ChatColor.GOLD + args[i] + ChatColor.AQUA + " is already in another " + ChatColor.RED + "party!" + ChatColor.AQUA + " Or has another " + ChatColor.RED + "invite!");
                    continue;
                }
                group.members.add(Bukkit.getServer().getPlayer(args[i]).getName());
                Timer t = new Timer();
                ExpireTimer et = new ExpireTimer(player.getName(), args[i], this);
                t.schedule(et, expireTime * 1000);
                player.sendMessage(ChatColor.AQUA + "You Sent " + ChatColor.GOLD + args[i] + ChatColor.AQUA + " a party invite! This invite will expire in " + ChatColor.GOLD + expireTime + " Seconds!");
                Bukkit.getServer().getPlayer(args[i]).playSound(Bukkit.getPlayer(args[i]).getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 0.5F);
                Bukkit.getServer().getPlayer(args[i]).sendMessage(ChatColor.AQUA + "Hey" + ChatColor.GOLD + "! " + player.getName() + ChatColor.AQUA + " sent you a" + ChatColor.GOLD + " party invite" + ChatColor.AQUA + "! Type" + ChatColor.GOLD+ " /party accept " + ChatColor.AQUA + "to join their party! This invite will expire in " + ChatColor.GOLD + expireTime + " Seconds!");
                Bukkit.getServer().getPlayer(args[i]).setInvulnerable(false);

            } catch (Exception ex) {
                player.sendMessage(ChatColor.RED + "Error :(");
            }
        }
    }

    private void acceptHandler(Player player) {
        PartyGroup pg = findPartyForPlayer(player.getName());

        if(pg == null) {
            player.sendMessage(ChatColor.RED + "ur lonely.");
            return;
        }

        if(pg.members.contains(player.getName())) {
            pg.accepted.add(player.getName());
            player.sendMessage(ChatColor.AQUA +"You joined " + ChatColor.LIGHT_PURPLE + pg.leader + "'s" + ChatColor.AQUA + " Party!");
            pg.members.remove(player.getName());
            Bukkit.getServer().getPlayer(pg.leader).sendMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " joined your party!");
        }
    }

    private void leaveHandler(Player player) {
        PartyGroup pg = findPartyForPlayer(player.getName());
        if(pg == null) {
            player.sendMessage(ChatColor.RED + "ur still lonely.");
            return;
        }

        if(pg.leader == player.getName()) {
            if(pg.accepted.size()<=0) {
                parties.remove(pg.leader);
                player.sendMessage(ChatColor.AQUA + "You left the party!");
                return;
            }
            Bukkit.getServer().getPlayer(pg.leader).sendMessage(ChatColor.AQUA + "You left the party!");
            parties.remove(pg.leader);
            pg.leader = pg.accepted.get(0);
            parties.put(pg.leader, pg);
            for(int i=0;i<pg.accepted.size();i++) {
                Bukkit.getServer().getPlayer(pg.accepted.get(i)).sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.AQUA + " left the party and " + ChatColor.GOLD + pg.leader + ChatColor.AQUA + " is the new " + ChatColor.LIGHT_PURPLE+ " party leader!");
            }
            pg.accepted.remove(pg.leader);
        }
        else {
            pg.accepted.remove(player.getName());
            player.sendMessage(ChatColor.AQUA + "You left the party!");
            Bukkit.getServer().getPlayer(pg.leader).sendMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " left the party :(");
            for(int i=0;i<pg.accepted.size();i++) {
                Bukkit.getServer().getPlayer(pg.accepted.get(i)).sendMessage(ChatColor.GOLD + player.getName() + ChatColor.AQUA + " left the party :(");
            }
        }
    }

    private void listHandler(Player player) {
        PartyGroup pg = findPartyForPlayer(player.getName());
        if(pg == null) {
            Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.AQUA + "-------------------------------");
            Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.GOLD + "Party Members" + ChatColor.WHITE + ":");
            Bukkit.getServer().getPlayer(player.getName()).sendMessage("");
            Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.AQUA + "-------------------------------");
            return;
        }

        Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.AQUA + "-------------------------------");
        Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.GOLD + "Party Members" + ChatColor.WHITE + ":");
        player.sendMessage(ChatColor.LIGHT_PURPLE + pg.leader);
        for(int j=0;j<pg.accepted.size();j++) {
            player.sendMessage(ChatColor.AQUA + pg.accepted.get(j));
        }
        Bukkit.getServer().getPlayer(player.getName()).sendMessage(ChatColor.AQUA + "-------------------------------");
    }

    private void helpHandler(Player player) {
        player.sendMessage(ChatColor.AQUA + "-------------------------------");
        player.sendMessage(ChatColor.GOLD + "/party <player> " + LIGHT_PURPLE + "- " + ChatColor.AQUA + "Send an invite to the desired player!");
        player.sendMessage(ChatColor.GOLD + "/party accept " + LIGHT_PURPLE + "- " + ChatColor.AQUA + "Accept party invite!");
        player.sendMessage(ChatColor.GOLD + "/party leave " + ChatColor.LIGHT_PURPLE + "- " + ChatColor.AQUA + "Leave a party!");
        player.sendMessage(ChatColor.GOLD + "/party kick <player> " + ChatColor.LIGHT_PURPLE + "- " + ChatColor.AQUA + "Kick someone form your party!");
        player.sendMessage(ChatColor.GOLD + "/party list " + ChatColor.LIGHT_PURPLE + "- " + ChatColor.AQUA + "List members in your party!");
        player.sendMessage(ChatColor.GOLD + "/party help " + ChatColor.LIGHT_PURPLE + "- " + ChatColor.AQUA + ":)");
        player.sendMessage(ChatColor.AQUA + "-------------------------------");
    }

    private void debugHandler(Player player) {
        for(int i=0;i<parties.size();i++) {
            player.sendMessage(parties.toString());
        }
    }

    private boolean isInOtherParty(String player, boolean checkLeader) {
        for(Map.Entry<String, PartyGroup> pg : parties.entrySet()) {
            // Is he leader?
            if(checkLeader && player == pg.getKey()) {
                return true;
            }

            // Is he member?
            for(int i=0;i<pg.getValue().members.size();i++) {
                if(player.equalsIgnoreCase(pg.getValue().members.get(i).toString())) {
                    return true;
                }
            }

            // Is he accepted?
            for(int i=0;i<pg.getValue().accepted.size();i++) {
                if(player.equalsIgnoreCase(pg.getValue().accepted.get(i).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public PartyGroup findPartyForPlayer(String player) {
        for(Map.Entry<String, PartyGroup> entry : parties.entrySet()) {
            if(player == entry.getKey()) {
                return entry.getValue();
            }

            for(int i=0;i<entry.getValue().members.size();i++) {
                if(player.equalsIgnoreCase(entry.getValue().members.get(i))) {
                    return entry.getValue();
                }
            }

            for(int i=0;i<entry.getValue().accepted.size();i++) {
                if (player.equalsIgnoreCase(entry.getValue().accepted.get(i))) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private void kickHandler(String player, String member) {
        if(player == null || member == null || player == "" || member == "") {
            return;
        }

        PartyGroup pg = findPartyForPlayer(player);

        if(pg == null) {
            Bukkit.getServer().getPlayer(player).sendMessage(RED + "Error? (sorry im new)");
            return;
        }

        if(pg.leader != player) {
            Bukkit.getServer().getPlayer(player).sendMessage(RED + "Your not the party leader!");
            return;
        }

        if(!pg.accepted.contains(member)) {
            Bukkit.getServer().getPlayer(player).sendMessage(RED + "are you for real rn?  (/party list)");
            return;
        }

        pg.accepted.remove(member);
        Bukkit.getServer().getPlayer(player).sendMessage(AQUA + "You have kicked " + RED + member + AQUA + " from your " + GOLD + "party!");
        Bukkit.getServer().getPlayer(member).sendMessage(GOLD + player + RED + " kicked " + AQUA + "you from the " + GOLD + "party! " + AQUA + ":O");
    }



}