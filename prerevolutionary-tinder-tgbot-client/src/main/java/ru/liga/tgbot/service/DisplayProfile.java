package ru.liga.tgbot.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.liga.tgbot.cache.PersonCache;
import ru.liga.tgbot.model.Person;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class DisplayProfile {
    PersonCache personCache;
    ProfileService profileService;


    String displayProfileWithText(Long userId) {
        Person person = personCache.getUsersCurrentPerson(userId);
        try {
            return "Ваша анкета!\n\nИмя:" + "\n" + profileService.translate(person.getName()).getText() + "\n" +
                    "Описание:" + "\n" + profileService.translate(person.getDescription().toString()).getText() + "\n";
                    //"Результат:" + Arrays.toString(profileService.profileToPicture(person.getName() + person.getDescription().toString())).substring(1,40);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "упс";
    }

//    File displayProfileWithPicture(Long userId) {
//        Person person = personCache.getUsersCurrentPerson(userId);
//        return
//    }

    @SneakyThrows
    void displayFile(){
        File file = new File("src/main/resources/prerev-background.jpg");
        BufferedImage image = ImageIO.read(file);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.setFont(new Font("Courier New", Font.BOLD, 50));
        g.drawString("Какой-то текст", 20, 50);
        ImageIO.write(image, "jpg", new File(file.getParentFile(), "фото_гор_с_текстом.jpg"));
    }

}
