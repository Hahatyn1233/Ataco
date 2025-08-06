package org.hahatyn.ataco.rpg.attribute;

public enum AttributeType {

    PHYSICAL_DAMAGE("§7Физический урон:"),
    MAGICAL_DAMAGE("§7Магический урон:"),
    PHYSICAL_ARMOR("§7Физическая броня:"),
    MAGICAL_ARMOR("§7Магическая броня:"),
    DODGE_CHANGE("§7Шанс увернуться:"),
    CRITICAL_DAMAGE("§7Критический урон:"),
    CRITICAL_CHANGE("§7Шанс критического урона:"),
    MOVESPEED("§7Скорость передвижения:"),
    VAMPIRISM("§7Вампиризм:"),
    EXP_BONUS("§7Дополнительный опыт:"),
    GOLD_BONUS("§7Дополнительное золото:"),
    MINER_BONUS("§7Дополнительная добыча:"),
    REGENERATION_HEALTH("§7Регенерация здоровья:"),
    REGENERATION_MANA("§7Регенерация маны:"),
    REGENERATION_STAMINA("§7Регенерация выносливости:"),
    MAX_HEALTH("§7Максимальное здоровье:"),
    MAX_MANA("§7Максимальная мана:"),
    MAX_STAMINA("§7Максимальная выносливость:"),
    STRENGTH("§7Сила:"),
    AGILITY("§7Ловкость:"),
    INTELLIGENCE("§7Интеллект:");


    private final String displayName;

    AttributeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
