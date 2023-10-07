package vdatta.us.danielbox.boxcore.menus;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import vdatta.us.danielbox.DanielBox;
import vdatta.us.danielbox.boxcore.services.values.MineChangeValues;
import vdatta.us.danielbox.mines.MineUtils;
import vdatta.us.danielbox.mines.mine.Mine;
import vdatta.us.danielbox.util.item.ItemFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static vdatta.us.danielbox.DanielBox.*;
import static vdatta.us.danielbox.boxcore.services.MineChangeName.changeMine;
import static vdatta.us.danielbox.mines.MineUtils.toggleBroadcast;
import static vdatta.us.danielbox.util.menu.MenuUtils.slot;
import static vdatta.us.danielbox.util.messaging.MessageComponent.format;
import static vdatta.us.danielbox.util.utilities.Utils.*;

public class MineMenu {


    protected static boolean generalBoolean = false;

    public static void create(Player player) {
        menuManager.createMenu(player, "&8» Minas", 9 * 6, true);
        menuManager.setContents(player, () -> {
            menuManager.setItem(player, slot(3, 3), new ItemFactory(Material.MAP, "&eLista de minas",

                    "",
                    "&8 ● &7Al dar click abriras el menu",
                    "&8 ● &7de minas activas.",
                    "",
                    "&e¡Click para abrir la lista de minas!").build(), () -> {
                minesMenu(player, 0);
            });

            setGeneralBooleanItem(player);
            setTaskItem(player);

            menuManager.setItem(player, slot(4, 3), new ItemFactory(Material.REPEATER, "&eRestablece todas las minas",
                    "",
                    "&8 ● &7Al dar click aqui restableceras los",
                    "&8 ● &7bloques de todas las minas.",
                    "",
                    "&8 ● &fMinas activas: &a" + mineManager.getMines().size(),
                    "",
                    "&e¡Click para restablecer todas las minas!").build(), () -> {

                player.sendMessage(" ");
                for (Mine mine : mineManager.getMines()) {
                    mineManager.resetMineNoBroadcast(mine);
                    player.sendMessage(format("&8 ● &fRestableciste la mina " + mine.getMinePrefix() + "&f."));
                }

                player.sendMessage(format("&2 ● &aTodas las minas fueron reseteadas!"));
            });

            menuManager.setItem(player, slot(7, 3), new ItemFactory(Material.TNT_MINECART, "&cEliminar todas las minas",
                    "",
                    "&8 ● &7Al dar click eliminaras",
                    "&8 ● &7todas las minas creadas.",
                    "",
                    "&8 ● &fMinas activas: &a" + mineManager.getMines().size(),
                    "",
                    "&e¡Click para eliminar todas las minas!").build(), () -> {

                player.sendMessage(" ");
                Iterator<Mine> iterator = mineManager.getMines().iterator();
                while (iterator.hasNext()) {
                    Mine mine = iterator.next();
                    player.sendMessage(format("&c ● &fEliminaste la mina " + mine.getMinePrefix() + "&f."));
                    DanielBox.getMineConfiguration().set("mine-storage." + mine.getMineName(), null);
                    DanielBox.getMineConfiguration().safeSave();
                    iterator.remove();
                }


                player.sendMessage(format("&4 ● &cTodas las minas fueron eliminadas!"));
            });


            menuManager.setItem(player, slot(5, 6), new ItemFactory(Material.BARRIER, "&cCerrar menu", "").build(), () -> {
                player.closeInventory();
            });
        });
    }

    protected static void setTaskItem(Player player) {
        Material material = Material.FIREWORK_STAR;
        String title;
        Color color;
        Runnable action;

        if (mineScheduler.isTaskRunning()) {
            title = "&cCancelar tarea de minas";
            color = Color.LIME;
            action = () -> {
                mineScheduler.stopTask();
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                player.sendMessage(format("&e&lDanielBox &8» &cCancelaste la tarea llamada 'mine-task'."));
            };
        } else {
            title = "&aIniciar tarea de minas";
            color = Color.RED;
            action = () -> {
                mineScheduler.startTasks();
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                player.sendMessage(format("&e&lDanielBox &8» &aIniciaste la tarea de 'mine-task'."));
            };
        }

        menuManager.setItem(player, slot(5, 3), new ItemFactory(material, title,
                "",
                "&8 ● &7Estado: " + formatBoolean(getMineScheduler().isTaskRunning(), "&aPrendido", "&cApagado"),
                "",
                "&8 ● &7Alterna el estado de la tarea",
                "&8 ● &7de las minas del servidor.",
                "",
                "&e¡Click para alternar!").buildFireworkStar(color), action);
    }

    protected static void setGeneralBooleanItem(Player player) {
        Color color;

        if (!generalBoolean) {
            color = Color.RED;
        } else color = Color.LIME;


        menuManager.setItem(player, slot(6, 3), new ItemFactory(Material.FIREWORK_STAR, "&eAlterna el anuncio de todas minas",
                "",
                "&8 ● &7Al dar click aqui alternaras los",
                "&8 ● &7anuncios de reseteo de las minas.",
                "",
                "&8 ● &fEstado general: &a" +generalBoolean,
                "",
                "&e¡Click para alternar!").buildFireworkStar(color), () -> {

                    for (Mine mine : mineManager.getMines()) {
                        MineUtils.toggleBroadcast(mine, !generalBoolean);
                    }

                    generalBoolean = !generalBoolean;
                    player.sendMessage(format("&8 ● &fAlternaste los anuncios de reseteos general a: "+formatBoolean(generalBoolean, "&aActivado" ,"&cDesactivado")+"."));
        });
    }
    public static void minesMenu(Player player, int page) {

        List<Mine> list = mineManager.getMines();

        final List<Integer> blockedSlots = Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53
        );

        menuManager.createMenu(player, "&9&l» &rLista de minas &8(Pág: " + (page + 1+ ")"), 9 * 6, true);
        int menuSize = player.getOpenInventory().getTopInventory().getSize();
        menuManager.setContents(player, () -> {


            if (page != 0)
                menuManager.setItem(player, slot(1, 6), new ItemFactory(Material.ARROW, "&ePágina anterior", "&7¡Click para volver ala página anterior!").build(), () -> {
                    minesMenu(player, page - 1);
                });



            menuManager.setItem(player, slot(5, 6), new ItemFactory(Material.BARRIER, "&cVolver atras", "&7¡Click para volver atras!").build(), () -> {
                create(player);
            });
            menuManager.setItem(player, slot(9, 6), new ItemFactory(Material.ARROW, "&ePágina siguiente", "&7¡Click para ir ala siguiente página!").build(), () -> {
                minesMenu(player, page + 1);
            });

            int startIndex = page * (menuSize - blockedSlots.size());
            int endIndex = Math.min(startIndex + (menuSize - blockedSlots.size()), list.size());

            int slot = 0;

            for (int i = startIndex; i < endIndex; i++) {
                Mine mine = list.get(i);

                while (isSlotDisabled(slot, blockedSlots)) {
                    slot++;
                }

                if (slot >= menuSize) {
                    break;
                }


                menuManager.setItem(player, slot, new ItemFactory(mine.getMineMaterials().get(0), "&b&lInformación de " + mine.getMineName(),
                        "",
                        "&8● &7Configura la mina '" + mine.getMineName() + "'.",
                        "",
                        "&f Tiempo de reseteo: &e" + formatTime(mine.getTime()),
                        "&f Anuncio de reseteo: &6" + mine.getBroadcastReset(),
                        "",
                        "&e Ubicaciones:",
                        "&8 » &fCentro: &a" + locationToString(mine.getCenter()),
                        "&8 » &fpos1: &a" + locationToString(mine.getPos1()),
                        "&8 » &fpos2: &a" + locationToString(mine.getPos1()),
                        "",
                        "&e¡Click para abrir configuración").build(), () -> {
                    settingsMine(player, mine);
                });
                slot++;
            }
        });
    }
    public static boolean isSlotDisabled(int slot, List<Integer> disabledSlots) {
        for (int disabledSlot : disabledSlots) {
            if (slot == disabledSlot) {
                return true;
            }
        }
        return false;
    }
    public static void settingsMine(Player player, Mine mine) {
        menuManager.createMenu(player, "&9&l» &rConfigurando mina '" + mine.getMineName()+"'", 9 * 5, true);
        menuManager.setContents(player, () -> {

            menuManager.setItem(player, slot(2, 2), new ItemFactory(Material.ENDER_EYE, "&bTeletransportarte",
                    "",
                    "&8 ● &7Diriguete al centro de la mina "+mine.getMineName()+".",
                    "&8 ● &7&oUbicación central: "+locationToString(mine.getCenter()),
                    "",
                    "&e¡Click para teletransportarte!").build(), () -> {
                player.teleport(centerSafeLocations(mine.getPos1(), mine.getPos2()));
                player.sendMessage(format("&e&lDanielBox &8» &aFuiste teletransportado ala mina '" + mine.getMineName() + "'."));
            });

            menuManager.setItem(player, slot(3, 2), new ItemFactory(Material.RED_DYE, "&cEliminar mina",
                    "",
                    "&8 ● &7Esta acción no tiene recuperacion",
                    "&8 ● &7por lo tanto no podras recuperar",
                    "&8 ● &7la mina luego de borrarla.",
                    "",
                    "&e¡Click para eliminar mina!").build(), () -> {
                mineManager.removeMine(mine);
                player.sendMessage(format("&e&lDanielBox &8» &cEliminaste la mina '" + mine.getMineName() + "'."));
                minesMenu(player, 0);
            });

            menuManager.setItem(player, slot(4, 2), new ItemFactory(Material.LAVA_BUCKET,"&6Resetear mina",
                    "",
                    "&8 ● &7Restablece los bloques de la region",
                    "&8 ● &7de la mina '"+mine.getMineName()+"'.",
                    "",
                    "&e¡Click para eliminar mina!").build(), () -> {

                mineManager.resetMine(mine);
                player.sendMessage(format("&e&lDanielBox &8» &eReseteaste la mina '" + mine.getMineName() + "'."));
            });


            menuManager.setItem(player, slot(5, 2), new ItemFactory(Material.REPEATER, "&bAlternar anuncio",
                    "",
                    "&8 ● &7Al darle click alternaras el",
                    "&8 ● &7anuncio de reseteo de esta mina",
                    "",
                    "&8 ● &fAnuncio de reseteo: &e" + formatBoolean(mine.getBroadcastReset(), "&aActivado", "&cDesactivado"),
                    "",
                    "&e¡Click para alternar!").build(), () -> {
                toggleBroadcast(mine);
                player.sendMessage(format("&e&lDanielBox &8» &eAlterna el anuncio de reseteo de la mina '" + mine.getMineName() + "' a " + mine.getBroadcastReset() + "."));
            });


            menuManager.setItem(player, slot(6, 2), new ItemFactory(Material.OAK_SIGN, "&aCambiar anuncio",
                    "",
                    "&8 ● &7Al darle click podras escribir",
                    "&8 ● &7el anuncio que quieres que salga",
                    "&8 ● &7cuando la mina sea reiniciada.",
                    "",
                    "&8 ● &fMensaje de reseteo: &e"+mine.getBroadcastMessage(),
                    "",
                    "&e¡Click para modificar!").build(), () -> {
                changeMine(player, mine, MineChangeValues.BroadcastMessage);
            });


            menuManager.setItem(player, slot(7, 2), new ItemFactory(Material.FILLED_MAP, "&aCambiar prefijo",
                    "",
                    "&8 ● &7Al darle click podras modificar",
                    "&8 ● &7el prefijo de la mina "+mine.getMineName()+".",
                    "",
                    "&8 ● &fPrefijo actual: &e"+mine.getMinePrefix(),
                    "",
                    "&e¡Click para modificar!").build(), () -> {
                changeMine(player, mine, MineChangeValues.Prefix);
            });


            menuManager.setItem(player, slot(5, 5), new ItemFactory(Material.BARRIER, "&cVolver atras",
                    "&8 ● &7Vuelve al menú de minas haciendo click aquí",
                    "",
                    "&e¡Click para abrir menú!"
                    ).build(), () -> {
                minesMenu(player, 0);
            });

        });
    }
}