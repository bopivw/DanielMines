package vdatta.us.danielmines.utilities.menus.base;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import vdatta.us.danielbox.util.configuration.Configuration;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static vdatta.us.danielbox.util.utilities.Utils.formatList;


@SuppressWarnings("ALL")
public class MenuItemBuilder implements Listener {

    private JavaPlugin plugin;
    private String displayName;
    private List<String> lore;
    private Material material;

    public MenuItemBuilder(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public MenuItemBuilder() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public MenuItemBuilder(Material material, String displayName, String... lore) {
        this.displayName = displayName;
        this.lore = Arrays.asList(lore); // Usar Arrays.asList para evitar la creación de una nueva lista
        this.material = material;
    }

    public MenuItemBuilder(Material material, String displayName, ArrayList<String> lore) {
        this.displayName = displayName;
        this.lore = new ArrayList<>(lore);
        this.material = material;
    }

    public MenuItemBuilder(Material material, String displayName, List<String> lore) {
        this.displayName = displayName;
        this.lore = new ArrayList<>(lore);
        this.material = material;
    }

    private void setupItemMeta(ItemStack itemStack, ItemMeta itemMeta) {
        itemMeta.setDisplayName(format(displayName));
        itemMeta.setLore(formatList(lore));
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_DYE);
        itemMeta.setUnbreakable(true);
        itemStack.setItemMeta(itemMeta);
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        return itemStack;
    }

    public ItemStack buildEnchants(int power, Enchantment... enchantments) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        for (Enchantment enchant : enchantments) {
            itemMeta.addEnchant(enchant, power, true);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack buildCustomModelData(int customModelData) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        itemMeta.setCustomModelData(customModelData);
        return itemStack;
    }

    public ItemStack build(int count) {
        ItemStack itemStack = new ItemStack(material, count);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        return itemStack;
    }

    public ItemStack buildLeather(Color color) {
        ItemStack itemStack = new ItemStack(material);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);
        itemMeta.setColor(color);
        return itemStack;
    }

    public ItemStack buildPlayerHead(String playerName) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, skullMeta);
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        return itemStack;
    }

    public ItemStack buildFireworkStar(Color fireworkColor) {
        if (fireworkColor == null) {
            throw new IllegalArgumentException("Firework color cannot be null.");
        }

        ItemStack itemStack = new ItemStack(Material.FIREWORK_STAR);
        FireworkEffectMeta itemMeta = (FireworkEffectMeta) itemStack.getItemMeta();
        setupItemMeta(itemStack, itemMeta);

        FireworkEffect.Builder builder = FireworkEffect.builder();
        builder.withColor(fireworkColor);

        FireworkEffect effect = builder.build();
        itemMeta.setEffect(effect);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }


    public ItemStack buildPlayerHeadTexture(String textureUrl) {
        textureUrl = "http://textures.minecraft.net/texture/" + textureUrl;
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_DYE);
        setupItemMeta(skullItem, skullMeta);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String base64Texture = getBase64FromTextureUrl(textureUrl);
        profile.getProperties().put("textures", new Property("textures", base64Texture));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return skullItem;
    }

    private String getBase64FromTextureUrl(String textureUrl) {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/" + UUID.randomUUID().toString();
        String payload = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";

        byte[] encodedPayload = Base64.getEncoder().encode(payload.getBytes());
        return new String(encodedPayload);
    }

    public static ItemStack buildFromConfiguration(Configuration configuration, String path) {
        if (configuration == null || !configuration.contains(path)) {
            return null;
        }

        String displayName = configuration.getString(path + ".item.name");
        Material material = Material.matchMaterial(configuration.getString(path + ".item.material"));
        List<String> lore = configuration.getStringList(path + ".item.lore");
        Color fireworkColor = null;
        if (material == Material.FIREWORK_STAR && configuration.contains(path + ".item.color")) {
            String colorStr = configuration.getString(path + ".item.color");
            fireworkColor = Color.fromRGB(Integer.parseInt(colorStr, 16));
        }
        MenuItemBuilder itemFactory = new MenuItemBuilder(material, displayName, lore);
        if (fireworkColor != null) {
            return itemFactory.buildFireworkStar(fireworkColor);
        } else {
            return itemFactory.build();
        }
    }

    public static String format(String content) {
        content = replacePlaceholders(content);
        content = translateColors(content);
        content = hex(content);
        return content;
    }

    public static String format(Player player, String content) {
        content = replacePlaceholders(content);
        content = translateColors(content);
        content = hex(content);
        content = setPlayerPlaceholders(player, content);
        return content;
    }

    public static String format(String content, Object... objects) {
        content = replacePlaceholders(content, objects);
        content = translateColors(content);
        content = hex(content);
        return content;
    }

    public static String format(Player player, String content, Object... objects) {

        content = replacePlaceholders(content, objects);
        content = translateColors(content);
        content = hex(content);
        content = setPlayerPlaceholders(player, content);
        return content;
    }

    private static String replacePlaceholders(String content, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            content = content.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return content;
    }

    private static String translateColors(String content) {
        return ChatColor.translateAlternateColorCodes('&', content);
    }

    private static String setPlayerPlaceholders(Player player, String content) {
        return PlaceholderAPI.setPlaceholders(player, content);
    }

    private static String hex(String content) {

        if (content == null) {
            return "&#FFFFF";
        }

        final char colorChar = ChatColor.COLOR_CHAR;
        Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
        final Matcher matcher = HEX_PATTERN.matcher(content);
        final StringBuffer buffer = new StringBuffer(content.length() + 4 * 8);

        while (matcher.find()) {
            final String group = matcher.group(1);

            matcher.appendReplacement(buffer, colorChar + "x"
                    + colorChar + group.charAt(0) + colorChar + group.charAt(1)
                    + colorChar + group.charAt(2) + colorChar + group.charAt(3)
                    + colorChar + group.charAt(4) + colorChar + group.charAt(5));
        }

        return matcher.appendTail(buffer).toString();
    }
}
