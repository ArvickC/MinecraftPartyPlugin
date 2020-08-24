package me.crazybanana.parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyGroup {
    public List<String> members = new ArrayList<String>();
    public List<String> accepted = new ArrayList<String>();
    public String leader = "";

    public PartyGroup(String s) {
        leader = s;
    }



}
