package ru.liga.tgbot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.Person;
import ru.liga.tgbot.model.PreReformText;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class PersonService {

    public PersonDTO createPerson(Person person) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI url = new URI("http://localhost:8085/persons");
        PersonDTO objEmp = new PersonDTO(person);

        HttpEntity<PersonDTO> requestEntity = new HttpEntity<>(objEmp, headers);

        RestTemplate restTemplate = new RestTemplate();
        PersonDTO responseEntity = restTemplate.postForObject(url, requestEntity, PersonDTO.class);

        return responseEntity;
    }
}
