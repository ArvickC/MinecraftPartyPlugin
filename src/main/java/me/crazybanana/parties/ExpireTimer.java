package me.crazybanana.parties;

import me.crazybanana.parties.commands.party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor.*;
import org.bukkit.block.data.type.Bed;

import javax.print.DocFlavor;
import java.util.TimerTask;

import static org.bukkit.ChatColor.*;

public class ExpireTimer extends TimerTask {

    private String sender;
    private String invited;
    private party p;

    public ExpireTimer(String sender, String invited, party p) {
        this.sender = sender;
        this.invited = invited;
        this.p = p;
    }

    public void run() {
        PartyGroup pg = p.findPartyForPlayer(invited);

        if(pg == null) {
            return;
        }

        if(pg.members.contains(invited)) {
            pg.members.remove(invited);
            Bukkit.getServer().getPlayer(sender).sendMessage(AQUA + "Hey" + GOLD + "!" + AQUA + " Your party invite to " + GOLD + invited + RED + " expired!");
            Bukkit.getServer().getPlayer(invited).sendMessage(AQUA + "Hey" + GOLD + "! " + sender + AQUA + "'s invite " + RED + "expired!");
        }
    }
}
