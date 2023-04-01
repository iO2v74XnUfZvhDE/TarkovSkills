package me.io2.tarkovskills;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class TarkovSkills extends JavaPlugin {
    private static JavaPlugin plugin;
    private static boolean isStarting = false;
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        isStarting = true;
        // Plugin startup logic
        getLogger().info("Hi!");
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(),this);
        Bukkit.getPluginManager().registerEvents(new TarkovSkillsListener(),this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new TarkovSkillExpansion().register();
        }

        new Thread(() -> {
            try {
                while (isStarting) {
                    getLogger().info("Savind data...");
                    TarkovSkillsListener.data.forEach((uuid, skillData) -> {
                        FileConfiguration config = TarkovSkills.getPlugin().getConfig();
                        for (SkillData skillDatum : skillData) {
                            config.set(uuid + "." + skillDatum.name + ".currentxp", skillDatum.currentXp);
                            config.set(uuid + "." + skillDatum.name + ".currentlevel", skillDatum.currentLevel);
                        }
                    });
                    saveConfig();
                    Thread.sleep(60000);
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        isStarting = false;
        TarkovSkillsListener.data.forEach((uuid, skillData) -> {
            FileConfiguration config = TarkovSkills.getPlugin().getConfig();
            for (SkillData skillDatum : skillData) {
                config.set(uuid + "." + skillDatum.name + ".currentxp", skillDatum.currentXp);
                config.set(uuid + "." + skillDatum.name + ".currentlevel", skillDatum.currentLevel);
            }
        });
        saveConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("checkskill")) {
            if (sender instanceof Player) {
                SkillCheckMenu.open((Player) sender);
            } else {
                sender.sendMessage("§cAmongus");
            }
            return true;
        }
        return false;
    }
}
