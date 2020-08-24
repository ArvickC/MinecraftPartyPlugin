package me.crazybanana.parties;

import me.crazybanana.parties.commands.party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.Timer;

public class CommandListener implements Listener {

    private party p;
    private JavaPlugin plugin;

    public CommandListener(party p, JavaPlugin plugin) {
        this.p = p;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) throws InterruptedException {
        String hgjoincmd = "/hg join ";
        if(event.getPlayer() == null || event.getMessage() == null || event.getMessage().length() < hgjoincmd.length()+1 || !event.getMessage().contains(hgjoincmd)) {
            return;
        }

        PartyGroup pg = p.findPartyForPlayer(event.getPlayer().getName());


        String game = event.getMessage().substring(hgjoincmd.length());

        if(pg == null) {
            return;
        }

        Bukkit.getPlayer("CrazyBanana4Life").sendMessage(pg.leader);
        Bukkit.getPlayer("CrazyBanana4Life").sendMessage(event.getPlayer().getName());
        boolean whatever = pg.leader == event.getPlayer().getName();
        Bukkit.getPlayer("CrazyBanana4Life").sendMessage(Boolean.toString(whatever));

        if(pg.leader != event.getPlayer().getName()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Your not the party leader!");

            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
            scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    // Do something
                    event.getPlayer().performCommand("hg leave");
                }
            }, 20L);

            return;
        }

        for(int i=0;i<pg.accepted.size();i++) {
            Bukkit.getServer().getPlayer(pg.accepted.get(i)).performCommand("hg join " + game);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
    }
}