package org.hahatyn.ataco.rpg.skills;

import org.hahatyn.ataco.rpg.classes.ClassesType;

public enum SkillsType {

    SKILL1("Скилл1", "Мощный сука скилл", ClassesType.WARRIOR);

    private final String displayName;
    private final String description;
    private final ClassesType classesType;

    SkillsType(String displayName, String description, ClassesType classesType) {
        this.displayName = displayName;
        this.description = description;
        this.classesType = classesType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public ClassesType getClassesType() {
        return classesType;
    }
}
