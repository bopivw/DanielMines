package vdatta.us.danielbox.mines;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import vdatta.us.danielbox.boxcore.services.values.MineChangeValues;
import vdatta.us.danielbox.mines.mine.Mine;

import java.util.List;

import static vdatta.us.danielbox.DanielBox.mineManager;

public class MineUtils {
    public static int getFlatIntLocations(Location pos1, Location pos2) {
        int x1 = pos1.getBlockX();
        int z1 = pos1.getBlockZ();
        int x2 = pos2.getBlockX();
        int z2 = pos2.getBlockZ();
        int deltaX = Math.abs(x1 - x2);
        int deltaZ = Math.abs(z1 - z2);

        int flatDistance = deltaX + deltaZ;

        return flatDistance;
    }

    public static Mine findNearestMine(Player player) {
        List<Mine> mineList = mineManager.getMines();
        Location playerLocation = player.getLocation();
        Mine nearestMine = null;
        int maxFlatDistance = 10; // Define la distancia máxima plana permitida

        for (Mine mine : mineList) {
            Location mineLocation = mine.getCenter();
            int flatDistance = getFlatIntLocations(playerLocation, mineLocation);

            if (flatDistance <= maxFlatDistance) {
                // Si la distancia plana es menor o igual a la máxima permitida, es candidata
                if (nearestMine == null || flatDistance < getFlatIntLocations(playerLocation, nearestMine.getCenter())) {
                    nearestMine = mine;
                }
            }
        }

        return nearestMine;
    }


    public static void toggleBroadcast(Mine mine) {
        if (mine != null) {
            mine.setBroadcastReset(!mine.getBroadcastReset());
            mineManager.saveMine(mine);
        }
    }

    public static void toggleBroadcast(Mine mine, boolean value) {
        if (mine != null) {
            mine.setBroadcastReset(value);
            mineManager.saveMine(mine);
        }
    }

    public static void changeMineUtil(MineChangeValues values, Mine mine, String string) {
        if (mine != null) {
            if (values == MineChangeValues.BroadcastMessage) mine.setBroadcastMessage(string);
            if (values == MineChangeValues.Prefix) mine.setMinePrefix(string);
            mineManager.saveMine(mine);
        }
    }
}