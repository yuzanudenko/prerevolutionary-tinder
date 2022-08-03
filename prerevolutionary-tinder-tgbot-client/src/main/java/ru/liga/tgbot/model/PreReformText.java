package ru.liga.tgbot.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PreReformText implements Serializable {
    private String text;

    public PreReformText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "PreReformText{" +
                "text='" + text + '\'' +
                '}';
    }
}
