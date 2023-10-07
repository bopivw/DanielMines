package vdatta.us.danielbox.mines.mine;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

import static vdatta.us.danielbox.DanielBox.mineManager;

public class MineScheduler {

    public BukkitTask bukkitTask;
    public JavaPlugin instance;
    @Getter
    public Map<Mine, Integer> mineTimers = new HashMap<>();

    public MineScheduler(JavaPlugin instance) {
        this.instance = instance;
    }


    public boolean isTaskRunning() {
        return bukkitTask != null && !bukkitTask.isCancelled();
    }

    public void toggleTask() {
        if (isTaskRunning()) {
            stopTask();
            startTasks();
        } else startTasks();

    }


    public void stopTask() {
        bukkitTask.cancel();

        for (Map.Entry<Mine, Integer> entry : mineTimers.entrySet()) {
            Mine mine = entry.getKey();
            Integer timer = entry.getValue();
            mineTimers.put(mine, 1);

        }

        mineTimers.clear();
    }

    public void startTasks() {
        for (Mine mine : mineManager.getMines()) {
            if (mine != null) { 
                mineTimers.put(mine, mine.getTime());
                mineManager.resetMineNoBroadcast(mine);
            }
        }

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Mine mine : mineTimers.keySet()) {
                    if (mine != null) { 
                        if (mineTimers.containsKey(mine)) {
                            int timeCloned = mineTimers.get(mine);
                            if (timeCloned == 0) {
                                mineManager.resetMine(mine);
                                timeCloned = mine.getTime();
                            } else {
                                timeCloned--;
                            }
                            mineTimers.put(mine, timeCloned);
                        }
                    }
                }
            }
        }.runTaskTimer(instance, 0, 20L);
    }
    

    public int getInternalTimeMine(Mine mine) {
        return mineTimers.getOrDefault(mine, 0);
    }

    public void stopMineTask(Mine mine) {
        bukkitTask.cancel();

        Map<Mine, Integer> mineTimersClone = new HashMap<>(mineTimers);
        mineTimersClone.remove(mine);
        mineTimers.clear();

        for (Mine mm : mineTimersClone.keySet()) {
            int i = mineTimersClone.get(mm);
            mineTimers.put(mm, i);
        }

        startTasks();
    }
}