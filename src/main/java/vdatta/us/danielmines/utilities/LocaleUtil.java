package vdatta.us.danielmines;

import lombok.Getter;
import org.bukkit.entity.Player;
import vdatta.us.danielmines.utilities.configuration.Configuration;

import static vdatta.us.danielmines.utilities.StringUtils.format;

public class Locale {

    @Getter
    private String pluginLocale;
    private final Configuration languaje;

    public Locale(String pluginLocale, Configuration languaje) {
        this.pluginLocale = pluginLocale;
        this.languaje = languaje;
    }

    public void refreshLocale(String locale){
        this.pluginLocale = locale;
    }

    public String getMessageLocale(Player player, String key) {
        if (languaje == null) {
            return "<null-locale>";
        }

        String string = languaje.getString("locales." + pluginLocale + "."+ key, "<null-key>");
        string = format(string);
        if (player != null) {
            string = format(player, string);
        }
        return string;
    }

    public String getMessageLocale(String key) {
        return getMessageLocale(null, key);
    }
}