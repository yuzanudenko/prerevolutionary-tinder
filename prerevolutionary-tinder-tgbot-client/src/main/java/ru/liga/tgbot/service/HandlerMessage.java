package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.ButtonsCaptions;
import ru.liga.tgbot.model.Sex;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class HandlerMessage {

    private PersonCache personCache;
    private DisplayProfile displayProfile;
    private ProfileService profileService;

    public SendMessage handleSendMessage(Update update) throws IOException, URISyntaxException {
        String messageText = update.getMessage().getText();
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();


        log.info("New message from User:{}. userId: {}, chatId: {}, with text: {}",
                message.getFrom().getUserName(),
                userId,
                message.getChatId().toString(),
                message.getText());

        switch (messageText) {
            case "/start":
                if (!personCache.containsKey(userId)) {
                    return getSengMessageQuestionSex(message);
                }
        }

        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_PROFILE_INFO)) {
            String[] inputDescr = messageText.split("\n");
            if (inputDescr.length > 1) {
                return getSendMessageQuestionTypeSearch(messageText, message, userId);
            }
        }

        return SendMessage.builder().chatId(message.getChatId().toString()).text("Сорри, это не поддерживается").build();
    }

    public SendPhoto handleSendPhoto(Update update) throws IOException, URISyntaxException {
        String messageText = update.getMessage().getText();
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);
        if (botState.equals(BotState.SET_PROFILE_INFO)) {
            String[] inputDescr = messageText.split("\n");
            if (inputDescr.length > 6) {
                return getProfile(message);
            }
        }

        return null;
    }

    private SendMessage getSendMessageQuestionTypeSearch(String messageText, Message message, Long userId) throws IOException, URISyntaxException {
        personCache.setNameAndDesciption(messageText, userId);
        personCache.setNewState(userId, BotState.SET_TYPE_SEARCH);
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
                        .text(ButtonsCaptions.BI.getCaption())
                        .callbackData(Sex.BI + ":" + ButtonsCaptions.BI.getCaption())
                        .build()));

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .text("\n ❔Выберете теперь кого ищем!").build();
    }

    private SendPhoto getProfile(Message message) throws IOException, URISyntaxException {
        InputStream inputStream = new ByteArrayInputStream(profileService.profileToPicture("Никита Афанасьев 14 лет, пошлый любитель пенного"));
        File file = new File("prerevolutionary-tinder-tgbot-client/src/main/resources/image.jpg");
        InputFile inputFile = new InputFile(file);
        return SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(inputFile)
                .build();
    }

    private SendMessage getSengMessageQuestionSex(Message message) {
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

        personCache.addPersonCache(message.getChat().getId(), BotState.SET_SEX);

/*        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        row.add("Row 1 Button 1");
        row.add("Row 1 Button 2");
        row.add("Row 1 Button 3");

        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);*/

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Вы сударь иль сударыня?")
                //.replyMarkup(keyboardMarkup)
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }
}
