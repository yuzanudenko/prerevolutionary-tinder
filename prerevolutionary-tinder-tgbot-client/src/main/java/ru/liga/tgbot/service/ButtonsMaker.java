package ru.liga.tgbot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.ButtonsCaptions;
import ru.liga.tgbot.model.Sex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ButtonsMaker {
    public List<List<InlineKeyboardButton>> createButtonsForQuestionSex() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.MALE.getCaption())
                        .callbackData(Sex.MALE + ":" + ButtonsCaptions.MALE.getCaption())
                        .build(),
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.FEMALE.getCaption())
                        .callbackData(Sex.FEMALE + ":" + ButtonsCaptions.FEMALE.getCaption())
                        .build()));
        return buttons;
    }

    public List<List<InlineKeyboardButton>> createButtonsForQuestionTypeSearch() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.MALE.getCaption())
                        .callbackData(Sex.MALE + ":" + ButtonsCaptions.MALE.getCaption())
                        .build(),
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.FEMALE.getCaption())
                        .callbackData(Sex.FEMALE + ":" + ButtonsCaptions.FEMALE.getCaption())
                        .build(),
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.ALL.getCaption())
                        .callbackData(Sex.ALL + ":" + ButtonsCaptions.ALL.getCaption())
                        .build()));
        return buttons;
    }

    public List<List<InlineKeyboardButton>> createButtonsForGetMyProfile() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(Arrays.asList(
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.SEARCH.getCaption())
                        .callbackData(BotState.SEARCH.toString())
                        .build(),
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.EDIT.getCaption())
                        .callbackData(BotState.EDIT.toString())
                        .build(),
                InlineKeyboardButton.builder()
                        .text(ButtonsCaptions.FAVORITES.getCaption())
                        .callbackData(BotState.FAVORITES.toString())
                        .build()));
        return buttons;
    }

    public ReplyKeyboardMarkup createButtonsForGetProfile() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add(ButtonsCaptions.LEFT.getCaption());
        row.add(ButtonsCaptions.MENU.getCaption());
        row.add(ButtonsCaptions.RIGHT.getCaption());

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
}
