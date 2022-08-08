package ru.liga.tgbot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.tgbot.dto.TextToPictureDTO;
import ru.liga.tgbot.model.PreReformText;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class ProfileService {

    public PreReformText translate(String text) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI url = new URI("http://localhost:8080/translate");
        PreReformText objEmp = new PreReformText(text);

        HttpEntity<PreReformText> requestEntity = new HttpEntity<>(objEmp, headers);

        RestTemplate restTemplate = new RestTemplate();
        PreReformText responseEntity = restTemplate.postForObject(url, requestEntity, PreReformText.class);

        return responseEntity;
    }

    public byte[] profileToPicture(String text) throws URISyntaxException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI url = new URI("https://pict-serv-2-0-fixey-dev.apps.sandbox-m2.ll9k.p1.openshiftapps.com/pict");
        TextToPictureDTO objEmp = new TextToPictureDTO(text);

        HttpEntity<TextToPictureDTO> requestEntity = new HttpEntity<>(objEmp, headers);

        RestTemplate restTemplate = new RestTemplate();
        byte[] responseEntity = restTemplate.postForObject(url, requestEntity, byte[].class);

        Files.write(Paths.get("prerevolutionary-tinder-tgbot-client/src/main/resources/image.jpg"), responseEntity);
        return responseEntity;
    }
}
