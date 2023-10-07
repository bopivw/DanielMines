package vdatta.us.danielbox.mines.mine;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import vdatta.us.danielbox.DanielBox;
import vdatta.us.danielbox.mines.MineListener;
import vdatta.us.danielbox.util.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

import static vdatta.us.danielbox.CoreUtils.bukkitBroadcast;
import static vdatta.us.danielbox.DanielBox.mineScheduler;

public class MineManager {
    private final Plugin plugin;
    private final Configuration config;
    @Getter
    private final List<Mine> mines;

    public MineManager(Plugin plugin, Configuration configuration) {
        this.plugin = plugin;
        this.config = configuration;
        this.mines = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(new MineListener(), plugin);
        for (String s : configuration.getConfigurationSection("mine-storage").getKeys(false)) {
            Mine mine = loadMine(s);
            mines.add(mine);
        }
    }


    public void saveMine(Mine mine) {
        ConfigurationSection mineSection = config.createSection("mine-storage." + mine.getMineName());
        mineSection.set("name", mine.getMineName());
        mineSection.set("prefix", mine.getMinePrefix());
        mineSection.set("time", mine.getTime());
        mineSection.set("broadcastReset", mine.getBroadcastReset());
        mineSection.set("broadcastMessage", mine.getBroadcastMessage());

        List<String> materialNames = new ArrayList<>();
        for (Material material : mine.getMineMaterials()) {
            materialNames.add(material.name());
        }
        mineSection.set("materials", materialNames);

        ConfigurationSection locationSection = mineSection.createSection("locations");
        locationSection.set("pos1", mine.getPos1());
        locationSection.set("pos2", mine.getPos2());
        config.safeSave();

        if (mines.contains(mine)) mines.remove(mine);
        if (mineScheduler.mineTimers.containsKey(mine)) mineScheduler.mineTimers.remove(mine);

        mines.add(mine);
        mineScheduler.mineTimers.put(mine, mine.getTime());
        mineScheduler.toggleTask();
    }

    public Mine loadMine(String mineName) {
        ConfigurationSection mineSection = config.getConfigurationSection("mine-storage." + mineName);
        if (mineSection == null) {
            return null;
        }

        if (!mineSection.contains("name") || !mineSection.contains("prefix")
                || !mineSection.contains("time") || !mineSection.contains("locations")) {
            DanielBox.getInstance().getLogger().warning("La mina '" + mineName + "' no contiene información completa y no se cargará.");
            return null;
        }

        String name = mineSection.getString("name");
        String prefix = mineSection.getString("prefix");

        int time = mineSection.getInt("time");
        ConfigurationSection locationSection = mineSection.getConfigurationSection("locations");

        if (locationSection != null) {
            Location pos1 = locationSection.getLocation("pos1");
            Location pos2 = locationSection.getLocation("pos2");

            if (pos1 != null && pos2 != null) {
                boolean broadcastReset = mineSection.getBoolean("broadcastReset", false);
                String broadcastMessage = mineSection.getString("broadcastMessage");
                List<String> materialNames = mineSection.getStringList("materials");
                List<Material> materials = new ArrayList<>();

                for (String materialName : materialNames) {
                    try {
                        Material material = Material.valueOf(materialName);
                        materials.add(material);
                    } catch (IllegalArgumentException e) {
                        DanielBox.getInstance().getLogger().warning("Material desconocido en la mina '" + mineName + "': " + materialName);
                    }
                }

                return new Mine(name, prefix, pos1, pos2, time, broadcastReset, broadcastMessage, materials);
            }
        }
        return null;
    }

    public void resetMine(Mine mine) {
        Location pos1 = mine.getPos1();
        Location pos2 = mine.getPos2();
        List<Material> materials = mine.getMineMaterials();
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());


        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material bloque = materials.get((int) (Math.random() * materials.size()));
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(bloque);
                }
            }
        }
        if (mine.getBroadcastReset()) {
            String minePrefix = mine.getMinePrefix() != null ? mine.getMinePrefix() : "";
            String mineName = mine.getMineName() != null ? mine.getMineName() : "";
            String mineBroadcastMessage = mine.getBroadcastMessage();

            bukkitBroadcast(mineBroadcastMessage
                    .replace("{mineprefix}", minePrefix)
                    .replace("{minename}", mineName));

        }

    }

    public void resetMineNoBroadcast(Mine mine) {
        Location pos1 = mine.getPos1();
        Location pos2 = mine.getPos2();
        List<Material> materials = mine.getMineMaterials();
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());


        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material bloque = materials.get((int) (Math.random() * materials.size()));
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(bloque);
                }
            }
        }
    }


    public void removeMine(Mine mine) {
        mineScheduler.stopMineTask(mine);
        mineScheduler.mineTimers.remove(mine);
        mineScheduler.toggleTask();
        mines.remove(mine);
        config.set("mine-storage."+mine.getMineName(), null);
        config.safeSave();
    }
}