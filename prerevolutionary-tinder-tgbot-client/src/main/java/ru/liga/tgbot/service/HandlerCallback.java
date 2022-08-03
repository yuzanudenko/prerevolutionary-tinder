package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.Sex;

@Slf4j
@Component
@AllArgsConstructor
public class HandlerCallback {
    private PersonCache personCache;
    public SendMessage answerCallback(CallbackQuery callbackQuery) {

        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        Long userId = callbackQuery.getFrom().getId();
        BotState botState = personCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.SET_SEX)) {
            personCache.setNewState(userId, BotState.SET_PROFILE_INFO);
            personCache.setNewSex(userId, Sex.valueOf(param[0]));
            return SendMessage.builder().chatId(message.getChatId().toString()).text("Поздравляю " + param[1] + ", теперь введите вашу инфу и описание").build();
        }

        if (botState.equals(BotState.SET_TYPE_SEARCH)) {
            personCache.setNewState(userId, BotState.PROFILE_DONE);
            personCache.setTypeSearch(userId, Sex.valueOf(param[0]));
            return SendMessage.builder().chatId(message.getChatId().toString()).text("Окей, начинаю поиск").build();
        }
        return SendMessage.builder().chatId(message.getChatId().toString()).text("Сорри, это не поддерживается").build();
    }

    private AnswerCallbackQuery getAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackQuery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }
}
