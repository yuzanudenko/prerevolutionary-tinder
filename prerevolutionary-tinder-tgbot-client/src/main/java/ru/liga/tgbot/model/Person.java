package ru.liga.tgbot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Person {
    private Long id;
    private Sex sex;
    private String name;
    private StringBuilder description;
    private Sex typeSearch;
    private BotState botState;

}
