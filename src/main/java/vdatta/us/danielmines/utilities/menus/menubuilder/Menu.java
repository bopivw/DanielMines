package vdatta.us.danielmines.utilities.menus.base;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static vdatta.us.danielbox.util.menu.base.MenuItemBuilder.format;

public class Menu {
    private final Inventory inventory;
    private final MenuItem[] items;
    private long lastExecutionTime;

    private static final long COOLDOWN = 20L * 5;

    public Menu(String title, int size) {
        this.inventory = createInventory(null, size, format(title));
        this.items = new MenuItem[size];
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setItem(int slot, MenuItem menuItem) {
        inventory.setItem(slot, menuItem.getItemStack());
        items[slot] = menuItem;
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastExecutionTime >= COOLDOWN) {
            if (slot >= 0 && slot < items.length && items[slot] != null) {
                items[slot].executeAction(player);
                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
                this.lastExecutionTime = currentTime;
            }
        }
    }
}