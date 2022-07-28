package ru.liga.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.liga.server.model.Person;
import ru.liga.server.repository.PersonRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public void save(Person person) {
        personRepository.save(person);
    }

    public Person create(Person person) {
        return personRepository.saveAndFlush(person);
    }

    public Person findByPersonId(Long personId) {
        return personRepository.findByPersonId(personId);
    }

    public List<Person> findAllSuitablePersons(Long personId) {
        int count = this.findSuitablePersonsCount(personId);
        Pageable pageable = PageRequest.ofSize(count).withSort(Sort.by("id").ascending());
        return personRepository.findSuitablePersons(personId, pageable).getContent();
    }

    public Person findSuitablePerson(Long personId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 1, Sort.by("id").ascending());
        Page<Person> statePage = personRepository.findSuitablePersons(personId, pageable);
        return statePage.getContent().get(0);
    }

    public int findSuitablePersonsCount(Long personId) {
        return personRepository.findSuitablePersonsCount(personId);
    }
}
