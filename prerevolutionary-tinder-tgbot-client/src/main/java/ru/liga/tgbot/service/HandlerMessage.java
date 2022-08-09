package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.ButtonsCaptions;

import java.io.*;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class HandlerMessage {

    private PersonCache personCache;
    private DisplayProfile displayProfile;
    private ProfileService profileService;
    private ButtonsMaker buttonsMaker;
    private PersonService personService;

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
        if (botState.equals(BotState.DEF)) {
            PersonDTO person = personService.getPerson(userId);
            if (person != null) {
                return displayProfile.getMyProfile(message, person.getFullName() + " " + person.getDescription());
            }
        }
        if (botState.equals(BotState.SEARCH)) {
            if (messageText.equals(ButtonsCaptions.RIGHT.getCaption())) {
                PersonDTO personDTO = personService.getSuitablePerson(userId, 2);
                return displayProfile.getProfile(message, personDTO.getNameAndDescription());
            }
        }

        return null;
    }

    private SendMessage getSendMessageQuestionTypeSearch(String messageText, Message message, Long userId) throws IOException, URISyntaxException {
        personCache.setNameAndDesciption(messageText, userId);
        personCache.setNewState(userId, BotState.SET_TYPE_SEARCH);
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForQuestionTypeSearch();
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .text("\n ❔Выберете теперь кого ищем!").build();
    }

    private SendMessage getSengMessageQuestionSex(Message message) {
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForQuestionSex();
        personCache.addPersonCache(message.getChat().getId(), BotState.SET_SEX);

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Вы сударь иль сударыня?")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }
}
