package vdatta.us.danielbox.boxcore.services;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import vdatta.us.danielbox.DanielBox;
import vdatta.us.danielbox.boxcore.menus.MineMenu;
import vdatta.us.danielbox.boxcore.services.values.MineChangeValues;
import vdatta.us.danielbox.mines.mine.Mine;

import java.util.HashMap;
import java.util.Map;

import static vdatta.us.danielbox.mines.MineUtils.changeMineUtil;
import static vdatta.us.danielbox.util.messaging.MessageComponent.format;

public class MineChangeName implements Listener {
    private static final Map<Player, MineChangeValues> changeBroadcastMessageList = new HashMap<>();
    private static final Map<Player, Mine> mineStorage = new HashMap<>();

    public MineChangeName(JavaPlugin javaPlugin) {
        Bukkit.getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (changeBroadcastMessageList.containsKey(player)) {
            MineChangeValues mineChangeValues = changeBroadcastMessageList.get(player);
            Mine mine = mineStorage.get(player);

            if (mineChangeValues == MineChangeValues.BroadcastMessage) {
                changeMineUtil(MineChangeValues.BroadcastMessage, mine, message);
                player.sendMessage(format("&e&lDanielBox &8» &aEl anuncio se cambio a: " + mine.getBroadcastMessage()));
            }

            if ((mineChangeValues == MineChangeValues.Prefix)) {
                changeMineUtil(MineChangeValues.Prefix, mine, message);
                player.sendMessage(format("&e&lDanielBox &8» &aEl prefijo de la mina " + mine.getMineName() + " se modifico a: " + mine.getMinePrefix()));
            }


            Bukkit.getScheduler().runTaskLater(DanielBox.getInstance(), () -> {
                MineMenu.settingsMine(player, mine);
            },1L);

            changeBroadcastMessageList.remove(player);
            event.setCancelled(true);
        }
    }

    public static void changeMine(Player player, Mine mine, MineChangeValues mineChangeValues) {
        player.closeInventory();
        changeBroadcastMessageList.put(player, mineChangeValues);
        mineStorage.put(player, mine);

        if (mineChangeValues.equals(MineChangeValues.BroadcastMessage)) {

            player.sendMessage(" ");
            player.sendMessage(format("&8 » &aCambiando el anuncio de reseteo... &8(" + mine.getMineName() + ")"));
            player.sendMessage(format("&8 ● &7Utiliza {mineprefix} para obtener el prefijo"));
            player.sendMessage(format("&8 ● &7y {minename} para obtener el nombre."));
            player.sendMessage(format("&8 ● &7&oEscribe el nuevo anuncio en el chat."));

            player.sendMessage(" ");
        }

        if (mineChangeValues.equals(MineChangeValues.Prefix)) {

            player.sendMessage(" ");
            player.sendMessage(format("&8 » &aCambiando el prefijo de la mina " + mine.getMineName() + "."));
            player.sendMessage(format("&8 ● &7Escribe el nuevo prefijo en el chat."));
            player.sendMessage(" ");
        }
    }
}