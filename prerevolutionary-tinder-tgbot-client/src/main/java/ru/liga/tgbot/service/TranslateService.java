package ru.liga.tgbot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.tgbot.model.PreReformText;


import java.net.URI;
import java.net.URISyntaxException;

@Service
public class TranslateService {

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
}
