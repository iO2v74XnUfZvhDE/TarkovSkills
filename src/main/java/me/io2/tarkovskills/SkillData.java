package me.io2.tarkovskills;

public class SkillData {
    public final String name;
    public int currentXp;
    public int currentLevel;
    public SkillData(String name, int currentXp, int currentLevel) {
        this.name = name;
        this.currentXp = currentXp;
        this.currentLevel = currentLevel;
    }
}
