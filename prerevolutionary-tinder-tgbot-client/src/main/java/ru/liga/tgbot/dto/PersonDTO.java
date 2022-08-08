package ru.liga.tgbot.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.liga.tgbot.model.Person;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {

    private String gender;
    private Long personId;
    private String fullName;
    private String description;
    private String genderSearch;

    public PersonDTO(Person person) {
        this.gender = person.getSex().toString();
        this.personId = person.getId();
        this.fullName = person.getName();
        this.description = person.getDescription().toString();
        this.genderSearch = person.getTypeSearch().toString();
    }
}
