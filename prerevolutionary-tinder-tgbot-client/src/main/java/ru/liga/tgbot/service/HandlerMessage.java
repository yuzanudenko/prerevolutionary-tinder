package ru.liga.tgbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.Action;
import ru.liga.tgbot.model.BotState;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@Component
public class HandlerMessage {

    @Autowired
    private PersonCache personCache;
    @Autowired
    private DisplayProfile displayProfile;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ButtonsMaker buttonsMaker;
    @Autowired
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

        if ("/start".equals(messageText)) {
            PersonDTO person = personService.getPerson(userId);
            if (!personCache.containsKey(userId) && person == null) {
                return getSengMessageQuestionSex(message);
            }
        }

        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_PROFILE_INFO)) {
            String[] inputDescrTypeFirst = messageText.split("\n");
            String[] inputDescrTypeSecond = messageText.split(" ");
            if (inputDescrTypeFirst.length > 1) {
                return getSendMessageQuestionTypeSearch(messageText, message, userId, "\n");
            }
            if (inputDescrTypeSecond.length > 1) {
                return getSendMessageQuestionTypeSearch(messageText, message, userId, " ");
            } else {
                return SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text("Введите на первой строке - ваше имя, на второй строке описание!, либо все в одной строке!")
                        .build();
            }
        }

        return SendMessage.builder().chatId(message.getChatId().toString()).text("Сорри, это не поддерживается \uD83D\uDE24").build();
    }

    public SendPhoto handleSendPhoto(Update update) throws IOException, URISyntaxException {
        String messageText = update.getMessage().getText();
        Message message = update.getMessage();
        Long userId = message.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);
        if (botState.equals(BotState.DEF)) {
            PersonDTO person = personService.getPerson(userId);
            if (person != null) {
                personCache.setPersonCache(userId, person);
                return displayProfile.getMyProfile(message, person.getFullName() + " " + person.getDescription());
            }
        }

        if (botState.equals(BotState.SEARCH)) {
            if (messageText.equals(Action.RIGHT.getCaption())) {
                personService.likePerson(userId, personCache.getLikedPersonId(userId));
                return getNextLikedProfile(message, userId);
            }
            if (messageText.equals(Action.LEFT.getCaption())) {
                return getNextLikedProfile(message, userId);
            }
            if (messageText.equals(Action.MENU.getCaption())) {
                personCache.setNewState(userId, BotState.PROFILE_DONE);
                personCache.resetPagesCounter(userId);
                return displayProfile.getMyProfile(message, personCache.getNameAndDescription(userId));
            }
        }
        if (botState.equals(BotState.FAVORITES)) {
            if (messageText.equals(Action.RIGHT.getCaption())) {
                return getNextFavoriteProfile(message, userId);
            }
            if (messageText.equals(Action.MENU.getCaption())) {
                personCache.setNewState(userId, BotState.PROFILE_DONE);
                personCache.resetPagesCounter(userId);
                return displayProfile.getMyProfile(message, personCache.getNameAndDescription(userId));
            }
            if (messageText.equals(Action.LEFT.getCaption())) {
                return getPrevFavoriteProfile(message, userId);
            }
        }

        return null;
    }

    private SendPhoto getNextLikedProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.incrementPagesCounter(userId);
        PersonDTO personDTO = personService.getSuitablePerson(userId, pagesCounter);
        personCache.setLikedPersonId(userId, personDTO.getPersonId());
        return displayProfile.getProfile(message, personDTO);
    }

    private SendPhoto getNextFavoriteProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.incrementPagesCounter(userId);
        PersonDTO personDTO = personService.getFavoritePerson(userId, pagesCounter);
        return displayProfile.getProfile(message, personDTO);
    }

    private SendPhoto getPrevFavoriteProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.minusPagesCounter(userId);
        PersonDTO personDTO = personService.getFavoritePerson(userId, pagesCounter);
        return displayProfile.getProfile(message, personDTO);
    }

    private SendMessage getSendMessageQuestionTypeSearch(String messageText, Message message, Long userId, String reg) throws IOException, URISyntaxException {
        personCache.setNameAndDescription(messageText, userId, reg);
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
