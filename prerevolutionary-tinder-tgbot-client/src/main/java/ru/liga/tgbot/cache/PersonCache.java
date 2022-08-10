package ru.liga.tgbot.cache;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.liga.tgbot.model.BotState;
import ru.liga.tgbot.model.Person;
import ru.liga.tgbot.model.Sex;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@ToString
@Component
public class PersonCache {

    private List<Person> persons = new ArrayList<>();

    public void addPersonCache(Long userId, BotState botState) {
        if (!containsKey(userId)) {
            log.info("Add to cache user: " + userId);
            persons.add(Person.builder()
                    .id(userId)
                    .botState(botState)
                    .pageCounter(1)
                    .build());
        }
    }

    public void setNameAndDesciption(String str, Long userId) {
        Person person = getUsersCurrentPerson(userId);
        String[] params = str.split("\n");
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                person.setName(params[0]);
            } else {
                if (person.getDescription() == null) {
                    person.setDescription(new StringBuilder().append(params[i]));
                } else {
                    person.setDescription(person.getDescription().append(" ").append(params[i]));
                }
            }
        }
        log.info("Set to user: " + userId + " name, descr " + person);
    }

    public String getNameAndDescription(Long userId) {
        Person result = getUsersCurrentPerson(userId);
        return result.getName() + " " + result.getDescription();
    }

    public void setNewState(Long userId, BotState botState) {
        for (Person person : persons) {
            if (person.getId().equals(userId)) {
                person.setBotState(botState);
            }
        }
        log.info("Set to user: " + userId + " state - " + botState);
    }

    public void setNewSex(Long userId, Sex sex) {
        Person person = getUsersCurrentPerson(userId);
        person.setSex(sex);
        log.info("Set to user: " + userId + " Sex - " + sex);
    }

    public void setTypeSearch(Long userId, Sex sex) {
        Person person = getUsersCurrentPerson(userId);
        person.setTypeSearch(sex);
        log.info("Set to user: " + userId + " TypeSearch - " + sex);
    }

    public BotState getUsersCurrentBotState(Long userId) {
        for (Person person : persons) {
            if (person.getId().equals(userId)) {
                return person.getBotState();
            }
        }
        return BotState.DEF;
    }

    public Person getUsersCurrentPerson(Long userId) {
        for (Person person : persons) {
            if (person.getId().equals(userId)) {
                return person;
            }
        }
        return Person.builder().id(userId).build();
    }

    public boolean containsKey(Long userId) {
        for (Person person : persons) {
            if (person.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public void setPages(Long userId, int counter) {
        Person person = getUsersCurrentPerson(userId);
        person.setPages(counter);
        log.info("Set to user: " + userId + " pages - " + counter);
    }

    public void setLikedPersonId(Long userId, Long likedPersonId) {
        Person person = getUsersCurrentPerson(userId);
        person.setLikedPersonId(likedPersonId);
        log.info("Set to user: " + userId + " likedPersonId - " + likedPersonId);
    }

    public Long getLikedPersonId(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        return person.getLikedPersonId();
    }

    public int incrementPagesCounter(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        int counter = person.getPageCounter();
        int pages = person.getPages();
        if (counter < pages) {
            person.setPageCounter(counter + 1);
        } else {
            person.setPageCounter(1);
        }
        int resultCounter = person.getPageCounter();
        log.info("Set to user: " + userId + " pagesCounter - " + resultCounter);
        return resultCounter;
    }
}
