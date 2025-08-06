package org.hahatyn.ataco.rpg.classes;

public enum ClassesType {

    WARRIOR("Воин", "Описание воина"),
    ARCHER("Лучник", "Описание лучника"),
    MAGE("Маг", "Описание мага");

    private final String displayName;
    private final String description;

    ClassesType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

}
