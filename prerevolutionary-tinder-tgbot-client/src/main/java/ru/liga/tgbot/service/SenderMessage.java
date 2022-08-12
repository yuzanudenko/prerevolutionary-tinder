package ru.liga.tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;

import java.util.List;

@Component
public class SenderMessage {
    @Autowired
    private ButtonsMaker buttonsMaker;
    @Autowired
    private PersonCache personCache;

    public SendMessage getSendMessageQuestionSex(Message message, Long userId) {
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForQuestionSex();
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("Вы сударь иль сударыня?")
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }

    public SendMessage getSendSuccessSetSex(String chatId, String sex) {
        return SendMessage.builder().chatId(chatId).text("Поздравляю " + sex + ", теперь введите вашу инфу и описание \uD83D\uDE0E").build();
    }

    public SendMessage getSendMessage(String chatId, String text) {
        return SendMessage.builder().chatId(chatId).text(text).build();
    }
}
