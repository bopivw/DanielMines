package vdatta.us.danielmines.utilities.menus.base;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


@SuppressWarnings("ALL")
public class MenuItem {
    private final ItemStack itemStack;
    private final Runnable action;

    public MenuItem(ItemStack itemStack, Runnable action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
    public Runnable getAction() {
        return action;
    }

    public void executeAction(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 1);
        this.action.run();
    }
}