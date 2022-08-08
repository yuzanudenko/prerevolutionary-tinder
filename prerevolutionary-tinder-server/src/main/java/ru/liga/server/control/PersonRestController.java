package ru.liga.server.control;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.dto.PersonDto;
import ru.liga.server.model.Person;
import ru.liga.server.service.PersonService;

import java.util.List;

@RestController
@RequestMapping("/persons")
@RequiredArgsConstructor
public class PersonRestController {

    private final PersonService personService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Person> findAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/{personId}")
    @ResponseStatus(HttpStatus.OK)
    public Person findAllPersons(@PathVariable Long personId) {
        return personService.findByPersonId(personId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody Person person) {
        personService.save(person);
    }

    @GetMapping("/{personId}/suitable")
    @ResponseStatus(HttpStatus.OK)
    public List<Person> findAllSuitablePersons(@PathVariable Long personId) {
        return personService.findAllSuitablePersons(personId);
    }

    @GetMapping("/{personId}/suitable/{page}")
    @ResponseStatus(HttpStatus.OK)
    public Person findSuitablePerson(@PathVariable Long personId, @PathVariable int page) {
        return personService.findSuitablePerson(personId, page);
    }

    @GetMapping("/{personId}/suitable/count")
    @ResponseStatus(HttpStatus.OK)
    public int getSuitablePersonsCount(@PathVariable Long personId) {
        return personService.findSuitablePersonsCount(personId);
    }

    @PostMapping("/{personId}/favorite")
    @ResponseStatus(HttpStatus.CREATED)
    public void likePerson(@PathVariable Long personId, @RequestBody Long likedPersonId) {
        personService.saveLikePerson(personId, likedPersonId);
    }

    @GetMapping("/{personId}/favorite")
    @ResponseStatus(HttpStatus.OK)
    public List<PersonDto> findAllFavoritePersons(@PathVariable Long personId) {
        return personService.findAllFavoritePersons(personId);
    }

    @GetMapping("/{personId}/favorite/{page}")
    @ResponseStatus(HttpStatus.OK)
    public PersonDto findFavoritePerson(@PathVariable Long personId, @PathVariable int page) {
        return personService.findFavoritePerson(personId, page);
    }

    @GetMapping("/{personId}/favorite/count")
    @ResponseStatus(HttpStatus.OK)
    public int getFavoritePersonsCount(@PathVariable Long personId) {
        return personService.getFavoritePersonsCount(personId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person createPerson(@RequestBody Person person) {
        return personService.create(person);
    }
}
