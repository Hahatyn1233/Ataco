package org.hahatyn.ataco.rpg.classes;

public class PlayerClassStats {

    private final String playerName;
    private ClassesType classType;
    private int level;
    private int experience;
    private int maxExperience;
    private int strength;
    private int agility;
    private int intelligence;
    private int statPoints;

    public PlayerClassStats(String playerName, ClassesType classType, int level, int experience, int maxExperience,
                            int strength, int agility, int intelligence, int statPoints) {
        this.playerName = playerName;
        this.classType = classType;
        this.level = level;
        this.experience = experience;
        this.maxExperience = maxExperience;
        this.strength = strength;
        this.agility = agility;
        this.intelligence = intelligence;
        this.statPoints = statPoints;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ClassesType getClassType() {
        return classType;
    }

    public void setClassType(ClassesType classType) {
        this.classType = classType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getMaxExperience() {
        return maxExperience;
    }

    public void setMaxExperience(int maxExperience) {
        this.maxExperience = maxExperience;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getStatPoints() {
        return statPoints;
    }

    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }
}