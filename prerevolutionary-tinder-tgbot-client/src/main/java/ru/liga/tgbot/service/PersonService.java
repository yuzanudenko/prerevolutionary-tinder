package ru.liga.tgbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.liga.tgbot.config.RestTemplateConfig;
import ru.liga.tgbot.dto.LikedPersonDTO;
import ru.liga.tgbot.dto.PersonDTO;
import ru.liga.tgbot.model.Person;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class PersonService {
    @Value("${person.url}")
    private String personsUrl;
    @Value("${person.url}")
    private String favoriteUrl;
    @Autowired
    private RestTemplateConfig restTemplateConfig;

    public PersonDTO createPerson(Person person) throws URISyntaxException {
        HttpHeaders headers = getHttpHeaders();
        URI url = new URI(personsUrl);
        PersonDTO objEmp = new PersonDTO(person);

        HttpEntity<PersonDTO> requestEntity = new HttpEntity<>(objEmp, headers);
        return restTemplateConfig.getRestTemplate().postForObject(url, requestEntity, PersonDTO.class);
    }

    public PersonDTO getPerson(Long userId) throws URISyntaxException {
        URI url = new URI(personsUrl + userId);
        return restTemplateConfig.getRestTemplate().getForObject(url, PersonDTO.class);
    }

    public PersonDTO getSuitablePerson(Long userId, int page) throws URISyntaxException {
        URI url = new URI(personsUrl + userId + "/suitable/" + page);
        return restTemplateConfig.getRestTemplate().getForObject(url, PersonDTO.class);
    }

    public PersonDTO getFavoritePerson(Long userId, int page) throws URISyntaxException {
        URI url = new URI(personsUrl + userId + "/favorite/" + page);
        return restTemplateConfig.getRestTemplate().getForObject(url, PersonDTO.class);
    }

    public Integer getCountSuitablePerson(Long userId) throws URISyntaxException {
        URI url = new URI(personsUrl + userId + "/suitable/count");
        return restTemplateConfig.getRestTemplate().getForObject(url, Integer.class);
    }

    public Integer getCountFavoritePerson(Long userId) throws URISyntaxException {
        URI url = new URI(personsUrl + userId + "/favorite/count");
        return restTemplateConfig.getRestTemplate().getForObject(url, Integer.class);
    }

    public void likePerson(Long userId, Long likedPersonId) throws URISyntaxException {
        HttpHeaders headers = getHttpHeaders();
        URI url = new URI(favoriteUrl);
        HttpEntity<LikedPersonDTO> requestEntity = new HttpEntity<>(new LikedPersonDTO(userId, likedPersonId), headers);
        restTemplateConfig.getRestTemplate().postForObject(url, requestEntity, LikedPersonDTO.class);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
