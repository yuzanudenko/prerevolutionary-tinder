package ru.liga.tgbot.service;

import org.springframework.stereotype.Component;
import ru.liga.tgbot.model.Person;
import ru.liga.tgbot.model.Sex;
import ru.liga.tgbot.repository.PersonsAll;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchPerson {
//
//    public List<Person> getPersonSex(Sex typeSearch) {
//        return PersonsAll.listPersons.stream().filter(person -> person.getTypeSearch().equals(typeSearch)).collect(Collectors.toList());
//    }
}
