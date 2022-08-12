package ru.liga.tgbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.Sex;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@Component
public class HandlerCallback {
    @Autowired
    private PersonCache personCache;
    @Autowired
    private PersonService personService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private DisplayProfile displayProfile;
    @Autowired
    private SenderMessage senderMessage;

    public SendMessage answerCallback(CallbackQuery callbackQuery) throws URISyntaxException {

        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        Long userId = callbackQuery.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_SEX)) {
            personCache.setNewState(userId, BotState.SET_PROFILE_INFO);
            personCache.setNewSex(userId, Sex.valueOf(param[0]));
            return senderMessage.getSendSuccessSetSex(message.getChatId().toString(), param[1]);
        }
        if (botState.equals(BotState.EDIT)) {
            personCache.setNewState(userId, BotState.SET_SEX);
            return senderMessage.getSendMessageQuestionSex(message, userId);
        }
        if (botState.equals(BotState.FAVORITES) && personCache.getPages(userId) == 0) {
            personCache.setNewState(userId, BotState.PROFILE_DONE);
            return senderMessage.getSendMessage(message.getChatId().toString(), "К сожалению у вас еще нету любимцев \uD83E\uDD7A");
        }
        return senderMessage.getSendMessage(message.getChatId().toString(), "Сорри, это не поддерживается \uD83D\uDE24");
    }

    public SendPhoto handleSendPhoto(CallbackQuery callbackQuery) throws IOException, URISyntaxException {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        Long userId = callbackQuery.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_TYPE_SEARCH)) {
            personCache.setNewState(userId, BotState.PROFILE_DONE);
            personCache.setTypeSearch(userId, Sex.valueOf(param[0]));
            personService.createPerson(personCache.getUsersCurrentPerson(userId));
            String text = personCache.getNameAndDescription(userId);
            return displayProfile.getMyProfile(message, text);
        }

        if (botState.equals(BotState.PROFILE_DONE)) {
            BotState newBotState = BotState.valueOf(param[0]);
            personCache.setNewState(userId, newBotState);
            if (newBotState.equals(BotState.SEARCH)) {
                setPagesCache(userId, personService.getCountSuitablePerson(userId));
                PersonDTO personDTO = personService.getSuitablePerson(userId, 1);
                personCache.setLikedPersonId(userId, personDTO.getPersonId());
                return displayProfile.getProfile(message, personDTO);
            }
            if (newBotState.equals(BotState.FAVORITES)) {
                int pagesCounter = personService.getCountFavoritePerson(userId);
                setPagesCache(userId, pagesCounter);
                if (pagesCounter > 0) {
                    PersonDTO personDTO = personService.getFavoritePerson(userId, 1);
                    return displayProfile.getProfile(message, personDTO);
                }
            }
        }
        return null;
    }

    private void setPagesCache(Long userId, int pagesCounter) {
        personCache.setPages(userId, pagesCounter);
        personCache.resetPagesCounter(userId);
    }
}
