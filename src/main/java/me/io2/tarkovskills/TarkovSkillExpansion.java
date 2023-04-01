package me.io2.tarkovskills;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class TarkovSkillExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "tarkovskill";
    }

    @Override
    public @NotNull String getAuthor() {
        return "iO2";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        FileConfiguration config = TarkovSkills.getPlugin().getConfig();
        if (config.getKeys(false).contains(player.getUniqueId().toString())) {
            String[] s = params.split("_");
            if (TarkovSkillsListener.data.containsKey(player.getUniqueId())) {
                List<SkillData> skillData = TarkovSkillsListener.data.get(player.getUniqueId());
                SkillData targetData = null;
                for (SkillData skillDatum : skillData) {
                    if (skillDatum.name.equalsIgnoreCase(s[0])) {
                        targetData = skillDatum;
                        break;
                    }
                }

                if (s[1].equalsIgnoreCase("currentxp")) {
                    return String.valueOf(targetData.currentXp);
                } else if (s[1].equalsIgnoreCase("currentlevel")) {
                    return String.valueOf(targetData.currentLevel);
                }
            } else {
                return String.valueOf(config.getInt(player.getUniqueId() + s[0] + s[1], 0));
            }
        }
        return null;
    }
}
