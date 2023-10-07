package vdatta.us.danielbox.util.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationManager {
    private JavaPlugin plugin;
    private final Map<String, Configuration> configurationMap;

    public ConfigurationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configurationMap = new HashMap<>();
    }

    public Configuration getConfig(final String name) {
        Configuration configuration = configurationMap.get(name);

        if (configuration != null) {
            return configuration;
        }

        final File configurationFile = new File(plugin.getDataFolder(), name);
        if (!configurationFile.exists()) {
            configurationFile.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        configuration = new Configuration(configurationFile);
        try {
            configuration.load();
            this.configurationMap.put(name, configuration);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }

        return configuration;
    }

    public Configuration getConfig(String folderName, String fileName) {
        File dataFolder = plugin.getDataFolder();
        File folder = new File(dataFolder, folderName);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File configFile = new File(folder, fileName);

        if (!configFile.exists()) {
            try {
                plugin.saveResource(folderName + File.separator + fileName, false);
            } catch (IllegalArgumentException ex) {
    ex.printStackTrace();
            }
        }

        return new Configuration(configFile);
    }




    public List<Configuration> configurationList() {
        return configurationMap.values().stream().collect(Collectors.toList());
    }
}