package vdatta.us.danielbox.mines.mine;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

import static vdatta.us.danielbox.util.utilities.Utils.centerLocations;
import static vdatta.us.danielbox.util.utilities.Utils.stringToLocation;

@SuppressWarnings("ALL")
public class Mine {

    @Getter
    public String mineName;
    @Getter @Setter
    public String minePrefix;

    @Getter
    public Location pos1;
    @Getter
    public Location pos2;

    @Getter
    @Setter
    public Boolean broadcastReset;
    @Getter
    @Setter
    public String broadcastMessage;

    @Getter @Setter
    public List<Material> mineMaterials;

    @Getter @Setter
    public int time;
    @Getter
    public int clonedTime;
    @Getter
    public Location center;

    public Mine(String mineName, Location pos1, Location pos2, int time, boolean broadcastReset, List<Material> materials) {
        this.mineName = mineName;
        this.minePrefix = "&e[" + mineName + "]";
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.time = time;
        this.clonedTime = time;
        this.broadcastReset = broadcastReset;
        this.mineMaterials = materials;
        this.broadcastMessage = "&e&lMinas &8» &fLa mina {mineprefix} &fse ha reseteado.";
        this.center = centerLocations(pos1, pos2);
    }

    public Mine(String mineName, String minePrefix, Location pos1, Location pos2, int time, boolean broadcastReset, String broadcastMessage, List<Material> materials) {
        this.mineName = mineName;
        this.minePrefix = minePrefix;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.time = time;
        this.clonedTime = time;
        this.broadcastReset = broadcastReset;
        this.mineMaterials = materials;
        this.broadcastMessage = broadcastMessage;
        this.center = centerLocations(pos1, pos2);
    }

    public Mine(String mineName, String pos1, String pos2, int time, boolean broadcastReset, Material[] materials) {
        this.mineName = mineName;
        this.minePrefix = "&e[" + mineName + "]";
        this.pos1 = stringToLocation(pos1);
        this.pos2 = stringToLocation(pos2);
        this.time = time;
        this.clonedTime = time;
        this.broadcastReset = broadcastReset;
        this.mineMaterials = List.of(materials);
        this.broadcastMessage = "&e&lMinas &8» &fLa mina {mineprefix} &fse ha reseteado.";
        this.center = centerLocations(this.pos1, this.pos2);
    }
}