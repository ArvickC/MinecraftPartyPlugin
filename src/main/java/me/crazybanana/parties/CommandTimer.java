package me.crazybanana.parties;

import org.bukkit.Bukkit;

import java.util.TimerTask;
import java.util.concurrent.Callable;

public class CommandTimer extends TimerTask {

    // Var.
    private String player;
    private String cmd;

    public CommandTimer(String player, String cmd) {
        this.player = player;
        this.cmd = cmd;
    }

    @Override
    public void run() {
        //Bukkit.getServer().getPlayer(player).performCommand(cmd);
    }
}
