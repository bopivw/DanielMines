package vdatta.us.danielbox.util.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import vdatta.us.danielbox.util.menu.base.Menu;
import vdatta.us.danielbox.util.menu.base.MenuItem;
import vdatta.us.danielbox.util.menu.base.MenuItemBuilder;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class MenuManager implements Listener {
    private final JavaPlugin plugin;
    private final Map<Player, Menu> openMenus = new HashMap<>();
    private final Map<Player, Runnable> menuRunnables = new HashMap<>();
    public static int updatedInt = 1;
    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        startMenuUpdater();
    }
    public void createMenu(Player player, String title, int size, boolean filled) {
        Menu menu = new Menu(title, size);

        if (filled) {
            for (int i = 0; i < size; i++) {
                if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                    menu.setItem(i, new MenuItem(new MenuItemBuilder(Material.GRAY_STAINED_GLASS_PANE, "&f", "&f").build(), () -> {
                    }));
                }
            }
        }

        player.openInventory(menu.getInventory());
        openMenus.put(player, menu);
    }
    public void setItem(Player player, int slot, ItemStack itemStack, Runnable action) {
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            if (slot >= 0 && slot < menu.getInventory().getSize()) {
                menu.setItem(slot, new MenuItem(itemStack, action));
            }
        }
    }
    public void setContents(Player player, Runnable runnable) {
        runnable.run();
        menuRunnables.put(player, runnable);
    }
    public void startMenuUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatedInt++;
                for (Player player : menuRunnables.keySet()) {
                    Runnable runnable = menuRunnables.get(player);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
    public void forceUpdateMenu(Player player) {
        Menu menu = openMenus.get(player);
        if (menu != null) {
            Runnable runnable = menuRunnables.get(player);
            if (runnable != null) {
                runnable.run();
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (openMenus.containsKey(player)) {
            Menu menu = openMenus.get(player);
            menu.handleClick(player, event.getRawSlot());
            forceUpdateMenu(player);
            event.setCancelled(true);
        }
    }
    public void closeInventoryActions(Player player) {
        openMenus.remove(player);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            closeInventoryActions(player);
        }
    }
    public void removeMenu(Player player) {
        openMenus.remove(player);
    }
}
