package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.Sex;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

@Slf4j
@Component
@AllArgsConstructor
public class HandlerCallback {
    private PersonCache personCache;
    private PersonService personService;
    private ProfileService profileService;

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

//        if (botState.equals(BotState.SET_TYPE_SEARCH)) {
//            personCache.setNewState(userId, BotState.PROFILE_DONE);
//            personCache.setTypeSearch(userId, Sex.valueOf(param[0]));
//            PersonDTO personDTO = personService.createPerson(personCache.getUsersCurrentPerson(userId));
//            log.info(personDTO.toString());
//            return SendMessage.builder().chatId(message.getChatId().toString()).text("Окей, начинаю поиск").build();
//        }
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
            return getProfile(message);
        }

        return null;
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
}
