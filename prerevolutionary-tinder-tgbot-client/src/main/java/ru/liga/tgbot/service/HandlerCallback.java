package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class HandlerCallback {
    private PersonCache personCache;
    private PersonService personService;
    private ProfileService profileService;
    private DisplayProfile displayProfile;

    public SendMessage answerCallback(CallbackQuery callbackQuery) throws URISyntaxException {

        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        Long userId = callbackQuery.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_SEX)) {
            personCache.setNewState(userId, BotState.SET_PROFILE_INFO);
            personCache.setNewSex(userId, Sex.valueOf(param[0]));
            return SendMessage.builder().chatId(message.getChatId().toString()).text("Поздравляю " + param[1] + ", теперь введите вашу инфу и описание").build();
        }

        return SendMessage.builder().chatId(message.getChatId().toString()).text("Сорри, это не поддерживается").build();
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

            return displayProfile.getMyProfile(message, personCache.getNameAndDescription(userId));
        }

        if (botState.equals(BotState.PROFILE_DONE)) {
            BotState newBotState = BotState.valueOf(param[0]);
            personCache.setNewState(userId, newBotState);

            PersonDTO personDTO = personService.getSuitablePerson(userId, 1);
            return displayProfile.getProfile(message, personDTO.getNameAndDescription());
        }


        return null;
    }
}
