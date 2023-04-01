package me.io2.tarkovskills;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class TarkovSkillsListener implements Listener {
    public static HashMap<UUID, List<SkillData>> data = new HashMap<>();
    private final HashMap<UUID, Double> playerMovedDists = new HashMap<>();
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        playerMovedDists.put(event.getPlayer().getUniqueId(), 0D);

        if (TarkovSkills.getPlugin().getConfig().getKeys(false).contains(event.getPlayer().getUniqueId().toString())) {
            ConfigurationSection configurationSection = TarkovSkills.getPlugin().getConfig().getConfigurationSection(event.getPlayer().getUniqueId().toString());
            if (configurationSection != null) {
                Set<String> keys = configurationSection.getKeys(false);
                List<SkillData> datas = new ArrayList<>();
                for (String key : keys) {
                    String baka = event.getPlayer().getUniqueId() + "." + key;
                    FileConfiguration config = TarkovSkills.getPlugin().getConfig();
                    datas.add(new SkillData(key, config.getInt(baka + ".currentxp"), config.getInt(baka + ".currentlevel")));
                }
                data.put(event.getPlayer().getUniqueId(), datas);
                return;
            }
        }

        List<SkillData> dataList = new ArrayList<>();
        dataList.add(new SkillData("Endurance", 0, 1));

        data.put(event.getPlayer().getUniqueId(), dataList);
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent event) {
        playerMovedDists.remove(event.getPlayer().getUniqueId());

        List<SkillData> skillData = data.get(event.getPlayer().getUniqueId());
        for (SkillData skillDatum : skillData) {
            FileConfiguration config = TarkovSkills.getPlugin().getConfig();
            config.set(event.getPlayer().getUniqueId() + "." + skillDatum.name + ".currentxp", skillDatum.currentXp);
            config.set(event.getPlayer().getUniqueId() + "." + skillDatum.name + ".currentlevel", skillDatum.currentLevel);
        }

        data.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().getName().contains("overworld")) return;

        double moveDist = playerMovedDists.get(event.getPlayer().getUniqueId());
        double v = event.getFrom().distanceSquared(event.getTo());
        // Bukkit.broadcastMessage(String.valueOf(v));
        if (v > 5) return;
        double v1 = moveDist + v;
        // Bukkit.broadcastMessage(String.valueOf(v1));
        playerMovedDists.replace(event.getPlayer().getUniqueId(), v1 % 10);
        if (v1 > 10) {
            SkillData targetData = null;
            for (SkillData skillData : data.get(event.getPlayer().getUniqueId())) {
                if (skillData.name.equalsIgnoreCase("Endurance")) {
                    targetData = skillData;
                    break;
                }
            }

            int enduranceEliteLevel = TarkovSkills.getPlugin().getConfig().getInt("EnduranceEliteLevel");
            if (targetData == null || targetData.currentLevel >= enduranceEliteLevel) return;

            targetData.currentXp += 1;
            double enduranceDefaultNeededXp = TarkovSkills.getPlugin().getConfig().getInt("EnduranceDefaultNeededXp") * targetData.currentLevel * 0.75;
            if (enduranceDefaultNeededXp < targetData.currentXp) {
                targetData.currentLevel = (int) (targetData.currentLevel + (targetData.currentXp / enduranceDefaultNeededXp));
                targetData.currentXp = (int) (targetData.currentXp % enduranceDefaultNeededXp);

                if (targetData.currentLevel >= enduranceEliteLevel) {
                    targetData.currentLevel = enduranceEliteLevel;
                    targetData.currentXp = 0;
                    event.getPlayer().sendMessage("§fYour endurance skill has been §aleveled up§f to §dELITE(" + targetData.currentLevel + ")§r");
                } else {
                    event.getPlayer().sendMessage("§fYour endurance skill has been §aleveled up to§f " + targetData.currentLevel);
                }
            }
        }
    }
}
