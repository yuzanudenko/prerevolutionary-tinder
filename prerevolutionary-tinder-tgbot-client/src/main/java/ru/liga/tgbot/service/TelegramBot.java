package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.config.BotConfig;


@Slf4j
@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private PersonCache personCache;
    private HandlerMessage handlerMessage;
    private HandlerCallback handlerCallback;


    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                handleMessage(update);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
        if (update.hasCallbackQuery()) {
            handleCallBack(update.getCallbackQuery());
        }
    }
    @SneakyThrows
    private void handleCallBack(CallbackQuery callbackQuery) {
            SendMessage message = handlerCallback.answerCallback(callbackQuery);
            execute(message);
    }

    private void handleMessage(Update update) throws TelegramApiException {
        SendMessage message = handlerMessage.handleMessage(update);
        execute(message);
    }
}
