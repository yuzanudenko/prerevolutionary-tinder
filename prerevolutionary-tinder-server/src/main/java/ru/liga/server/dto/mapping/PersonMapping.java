package ru.liga.server.dto.mapping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.server.dto.PersonDto;
import ru.liga.server.model.LikedPerson;
import ru.liga.server.model.Person;
import ru.liga.server.repository.LikedPersonRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonMapping {

    private final LikedPersonRepository likedPersonRepository;

    public PersonDto createModel(Person likedPerson, Long mainPersonId) {
        PersonDto personDto = new PersonDto();
        personDto.setId(likedPerson.getId());
        personDto.setPersonId(likedPerson.getPersonId());
        personDto.setFullName(likedPerson.getFullName());
        personDto.setGender(likedPerson.getGender());
        personDto.setGenderSearch(likedPerson.getGenderSearch());
        personDto.setDescription(likedPerson.getDescription());
        personDto.setStatus(getLikedPersonStatus(mainPersonId, likedPerson.getId()));
        return personDto;
    }

    public List<PersonDto> createModelList(List<Person> likedPersons, Long mainPersonId) {
        List<PersonDto> personDtoList = new ArrayList<>();

        for (Person likedPerson : likedPersons) {
            personDtoList.add(createModel(likedPerson, mainPersonId));
        }

        return personDtoList;
    }

    private String getLikedPersonStatus(Long mainPersonId, Long likedPersonId) {
        LikedPerson likePerson = likedPersonRepository.getByMainIdAndLikedId(mainPersonId, likedPersonId);
        LikedPerson likedMePerson = likedPersonRepository.getByMainIdAndLikedId(likedPersonId, mainPersonId);

        if (likePerson != null && likedMePerson != null) {
            return "RECIPROCITY";
        } else if (likePerson != null) {
            return "LIKE";
        } else {
            return "LIKED_ME";
        }
    }
}
