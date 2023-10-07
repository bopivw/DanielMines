package vdatta.us.danielbox.mines;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import vdatta.us.danielbox.boxcore.menus.MineMenu;
import vdatta.us.danielbox.mines.mine.Mine;

import java.util.List;

import static vdatta.us.danielbox.DanielBox.mineManager;
import static vdatta.us.danielbox.util.messaging.MessageComponent.format;


@CommandPermission("danielbox.operator")
@CommandAlias("mine|caves|mines|coremine|minas|caves|cave|mina")
public class MineCommand extends BaseCommand implements Listener {


    @Default
    void defaultCommand(Player player){
        new MineMenu().create(player);
    }

    @CommandCompletion("<Name> @range:1-30")
    @Subcommand("create")
    public void createMina(Player player, String name, int time, Material materials) {
        List<String> minesNames = mineManager.getMines().stream()
                .map(Mine::getMineName)
                .toList();

        if (minesNames.contains(name)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            player.sendMessage(format("&e&lMinas &8» &cEsta mina ya existe!"));
            return;
        }
        WorldEdit worldEdit = WorldEdit.getInstance();
        World world = BukkitAdapter.adapt(player.getWorld());
        Region region;
        try {
            region = worldEdit.getSessionManager().get(BukkitAdapter.adapt(player)).getSelection(BukkitAdapter.adapt(player.getWorld()));
        } catch (IncompleteRegionException e) {
            throw new RuntimeException(e);
        }
        BlockVector3 maximumPoint = region.getMaximumPoint();
        BlockVector3 minimumPoint = region.getMinimumPoint();

        Location pos1 = new Location(player.getWorld(), maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ());
        Location pos2 = new Location(player.getWorld(), minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());

        Mine mine = new Mine(name, pos1, pos2, time, false, List.of(materials));
        mineManager.saveMine(mine);
        mineManager.resetMine(mine);

        player.sendMessage(format("&e&lMinas &8» &aHas creado la mina {0}.", name));

    }
}