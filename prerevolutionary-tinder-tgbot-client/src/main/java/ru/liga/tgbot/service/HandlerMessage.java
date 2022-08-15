package ru.liga.tgbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.Action;
import ru.liga.tgbot.model.BotState;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Component
public class HandlerMessage {

    @Autowired
    private PersonCache personCache;
    @Autowired
    private SenderPhoto senderPhoto;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ButtonsMaker buttonsMaker;
    @Autowired
    private PersonService personService;
    @Autowired
    private SenderMessage senderMessage;

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
                return getSendMessageQuestionSex(message);
            }
        }

        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_PROFILE_INFO)) {
            return setProfileInfo(messageText, message, userId);
        }

        return senderMessage.getSendMessage(message.getChatId().toString(), "Сорри, это не поддерживается \uD83D\uDE24");
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
                return senderPhoto.getMyProfile(message, person.getFullName() + " " + person.getDescription());
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
                return getMenuAndProfileWithDescr(message, userId);
            }
        }
        if (botState.equals(BotState.FAVORITES)) {
            if (messageText.equals(Action.RIGHT.getCaption())) {
                return getNextFavoriteProfile(message, userId);
            }
            if (messageText.equals(Action.MENU.getCaption())) {
                return getMenuAndProfileWithDescr(message, userId);
            }
            if (messageText.equals(Action.LEFT.getCaption())) {
                return getPrevFavoriteProfile(message, userId);
            }
        }

        return null;
    }

    private SendMessage setProfileInfo(String messageText, Message message, Long userId) throws IOException, URISyntaxException {
        String[] inputDescrTypeFirst = messageText.split("\n");
        String[] inputDescrTypeSecond = messageText.split(" ");
        if (inputDescrTypeFirst.length > 1) {
            return getSendMessageQuestionTypeSearch(messageText, message, userId, "\n");
        }
        if (inputDescrTypeSecond.length > 1) {
            return getSendMessageQuestionTypeSearch(messageText, message, userId, " ");
        } else {
            return senderMessage.getSendMessage(message.getChatId().toString(),
                    "Введите на первой строке - ваше имя, на второй строке описание!, либо все в одной строке!");
        }
    }

    private SendPhoto getMenuAndProfileWithDescr(Message message, Long userId) throws IOException, URISyntaxException {
        personCache.setNewState(userId, BotState.PROFILE_DONE);
        personCache.resetPagesCounter(userId);
        return senderPhoto.getMyProfile(message, personCache.getNameAndDescription(userId));
    }

    private SendPhoto getNextLikedProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.incrementPagesCounter(userId);
        PersonDTO personDTO = personService.getSuitablePerson(userId, pagesCounter);
        personCache.setLikedPersonId(userId, personDTO.getPersonId());
        return senderPhoto.getProfile(message, personDTO);
    }

    private SendPhoto getNextFavoriteProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.incrementPagesCounter(userId);
        return senderPhoto.getProfile(message, personService.getFavoritePerson(userId, pagesCounter));
    }

    private SendPhoto getPrevFavoriteProfile(Message message, Long userId) throws URISyntaxException, IOException {
        int pagesCounter = personCache.minusPagesCounter(userId);
        return senderPhoto.getProfile(message, personService.getFavoritePerson(userId, pagesCounter));
    }

    private SendMessage getSendMessageQuestionTypeSearch(String messageText, Message message, Long userId, String reg) throws IOException, URISyntaxException {
        personCache.setNameAndDescription(messageText, userId, reg);
        personCache.setNewState(userId, BotState.SET_TYPE_SEARCH);
        return senderMessage.getSendMessageQuestionTypeSearch(message);
    }

    private SendMessage getSendMessageQuestionSex(Message message) {
        personCache.addPersonCache(message.getChat().getId(), BotState.SET_SEX);
        return senderMessage.getSendMessageQuestionSex(message);
    }
}
