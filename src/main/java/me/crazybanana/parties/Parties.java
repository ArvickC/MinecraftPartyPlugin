package me.crazybanana.parties;

import me.crazybanana.parties.commands.party;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Parties extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        party p = new party();

        getCommand("party").setExecutor(p);

        CommandListener commandListener = new CommandListener(p, this);
        getServer().getPluginManager().registerEvents(commandListener, this);
    }
}
