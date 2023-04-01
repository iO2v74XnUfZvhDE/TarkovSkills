package me.io2.tarkovskills;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.type.ChestMenu;

import java.util.Arrays;
import java.util.List;


public class SkillCheckMenu {
    public static void open(Player player) {
        ChestMenu skillCheckMenu = ChestMenu.builder(6)
                .title("SkillCheck Menu")
                .redraw(true)
                .build();
        List<SkillData> skillData = TarkovSkillsListener.data.get(player.getUniqueId());
        for (int i = 0; i < 54; i++) {
            skillCheckMenu.getSlot(i).setClickOptions(ClickOptions.DENY_ALL);
            skillCheckMenu.getSlot(i).setItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        }
        int i = 0;
        for (SkillData skillDatum : skillData) {
            ItemStack stack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(skillDatum.name);
            itemMeta.setLore(Arrays.asList(String.format("Current xp: %d/%.0f", skillDatum.currentXp, TarkovSkills.getPlugin().getConfig().getInt(skillDatum.name + "DefaultNeededXp") * skillDatum.currentLevel * 0.75), "Current Level: " + skillDatum.currentLevel));
            stack.setItemMeta(itemMeta);
            skillCheckMenu.getSlot(i).setItem(stack);
            i++;
        }

        skillCheckMenu.open(player);
    }
}
