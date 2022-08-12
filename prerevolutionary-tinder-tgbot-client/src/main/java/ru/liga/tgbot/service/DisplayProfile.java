package ru.liga.tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.Action;
import ru.liga.tgbot.model.PreReformText;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class DisplayProfile {
    @Autowired
    private PersonCache personCache;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ButtonsMaker buttonsMaker;
    @Value("${path.image}")
    private String filePath;


    public SendPhoto getMyProfile(Message message, String text) throws IOException, URISyntaxException {
        List<List<InlineKeyboardButton>> buttons = buttonsMaker.createButtonsForGetMyProfile();
        InputFile inputFile = getInputFile(text);
        return SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(inputFile)
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build();
    }

    public SendPhoto getProfile(Message message, PersonDTO personDTO) throws IOException, URISyntaxException {
        ReplyKeyboardMarkup keyboardMarkup = buttonsMaker.createButtonsForGetProfile();
        InputFile inputFile = getInputFile(getProfileText(personDTO));
        return SendPhoto.builder()
                .chatId(message.getChatId().toString())
                .photo(inputFile)
                .replyMarkup(keyboardMarkup)
                .build();
    }

    private InputFile getInputFile(String text) throws URISyntaxException, IOException {
        PreReformText preReformText = profileService.translate(text);
        profileService.profileToPicture(preReformText.getText());
        File file = new File(filePath);
        return new InputFile(file);
    }

    private String getProfileText(PersonDTO personDTO) {
        if (personDTO.getStatus() != null) {
            return personDTO.getFullName() + " - " + Action.valueOf(personDTO.getStatus()).getCaption() + "\n" + personDTO.getDescription();
        } else {
            return personDTO.getFullName()  + "\n" + personDTO.getDescription();
        }
    }
}
