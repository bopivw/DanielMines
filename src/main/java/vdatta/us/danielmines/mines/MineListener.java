package vdatta.us.danielbox.mines;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import vdatta.us.danielbox.mines.mine.Mine;

import static vdatta.us.danielbox.DanielBox.mineScheduler;
import static vdatta.us.danielbox.mines.MineUtils.findNearestMine;
import static vdatta.us.danielbox.util.messaging.MessageComponent.format;

public class MineListener implements Listener {
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Mine nearbyMine = findNearestMine(player);

        if (nearbyMine != null) {
            int internalTimeMine = mineScheduler.getInternalTimeMine(nearbyMine);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(format("&a{0}", internalTimeMine)));
        }
    }
}