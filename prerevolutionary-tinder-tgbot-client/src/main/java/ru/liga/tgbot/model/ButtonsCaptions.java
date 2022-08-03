package ru.liga.tgbot.model;

public enum ButtonsCaptions {
    MALE("Сударъ"),FEMALE("Сударыня"),BI("Всех"),LEFT("Влево"),RIGHT("Вправо"),MENU("Меню");

    private String caption;

    ButtonsCaptions(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
