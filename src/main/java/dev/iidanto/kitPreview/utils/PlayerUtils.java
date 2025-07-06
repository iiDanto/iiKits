package dev.iidanto.kitPreview.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {
    public static List<Player> getPlayersInDistance(Location location, double radius){
        List<Player> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            Location loc = player.getLocation();
            if (loc.getWorld() == location.getWorld() && loc.distance(location) <= radius){
                players.add(player);
            }
        });
        return players;
    }
}
