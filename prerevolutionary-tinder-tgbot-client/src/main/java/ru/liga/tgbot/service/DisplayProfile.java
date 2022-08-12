package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.PreReformText;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

@Component
@AllArgsConstructor
public class DisplayProfile {
    private PersonCache personCache;
    private ProfileService profileService;
    private ButtonsMaker buttonsMaker;

    public SendPhoto getMyProfile(Message message, String text) throws IOException, URISyntaxException {
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForGetMyProfile();
        PreReformText preReformText = profileService.translate(text);
        InputStream inputStream = new ByteArrayInputStream(profileService.profileToPicture(preReformText.getText()));
        File file = new File("prerevolutionary-tinder-tgbot-client/src/main/resources/image.jpg");
        InputFile inputFile = new InputFile(file);
        return SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(inputFile)
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }

    public SendPhoto getProfile(Message message, String text) throws IOException, URISyntaxException {
        ReplyKeyboardMarkup keyboardMarkup = buttonsMaker.createButtonsForGetProfile();
        PreReformText preReformText = profileService.translate(text);
        InputStream inputStream = new ByteArrayInputStream(profileService.profileToPicture(preReformText.getText()));
        File file = new File("prerevolutionary-tinder-tgbot-client/src/main/resources/image.jpg");
        InputFile inputFile = new InputFile(file);
        return SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(inputFile)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    public SendMessage getSengMessageQuestionSex(Message message, Long userId) {
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForQuestionSex();
        personCache.setNewState(userId, BotState.SET_SEX);

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Вы сударь иль сударыня?")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }
}
