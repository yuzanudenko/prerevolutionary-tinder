package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.config.BotConfig;

import java.io.IOException;
import java.net.URISyntaxException;


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
            } catch (TelegramApiException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (update.hasCallbackQuery()) {
            handleCallBack(update.getCallbackQuery());
        }
    }
    @SneakyThrows
    private void handleCallBack(CallbackQuery callbackQuery) {
        //execute(handlerCallback.answerCallback(callbackQuery));
        SendPhoto sendPhoto = handlerCallback.handleSendPhoto(callbackQuery);
        SendMessage sendMessage = handlerCallback.answerCallback(callbackQuery);
        if (sendPhoto != null) {
            execute(sendPhoto);
        } else {
            execute(sendMessage);
        }
    }

    private void handleMessage(Update update) throws TelegramApiException, IOException, URISyntaxException {
        SendPhoto sendPhoto = handlerMessage.handleSendPhoto(update);
        SendMessage sendMessage = handlerMessage.handleSendMessage(update);
        if (sendPhoto != null) {
            execute(sendPhoto);
        } else {
            execute(sendMessage);
        }
    }
}
