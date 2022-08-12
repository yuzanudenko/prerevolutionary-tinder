package ru.liga.tgbot.model;

public enum Action {
    MALE("Сударъ"),
    FEMALE("Сударыня"),
    ALL("Всех"),
    LEFT("Влево"),
    RIGHT("Вправо"),
    SEARCH("Поиск"),
    PROFILE("Анкета"),
    FAVORITES("Любимцы"),
    EDIT("Редактировать"),
    RECIPROCITY("Взаимность."),
    LIKED_ME("Вы любимы."),
    LIKE("Любим вами."),
    MENU("Меню");

    private String caption;

    Action(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
