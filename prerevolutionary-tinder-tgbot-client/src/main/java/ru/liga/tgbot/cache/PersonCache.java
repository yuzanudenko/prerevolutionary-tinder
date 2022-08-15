package ru.liga.tgbot.cache;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.liga.tgbot.dto.PersonDTO;
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

    /**
     * Добавление нового Person в кеш
     *
     * @param userId   Id пользака из телеграмма
     * @param botState Состояние для этого пользлователя
     */
    public void addPersonCache(Long userId, BotState botState) {
        if (!containsKey(userId)) {
            log.info("Add to cache user: " + userId);
            persons.add(Person.builder()
                    .personId(userId)
                    .botState(botState)
                    .pageCounter(1)
                    .build());
        }
    }

    /**
     * Добавление всех полей для данного Person из дто
     *
     * @param userId    Id пользака из телеграмма
     * @param personDTO DTO из ответа сервиса
     */
    public void setPersonCache(Long userId, PersonDTO personDTO) {
        Person person = getUsersCurrentPerson(userId);
        person.setId(personDTO.getId());
        person.setName(personDTO.getFullName());
        person.setDescription(new StringBuilder(personDTO.getDescription()));
        person.setSex(Sex.valueOf(personDTO.getGender()));
        person.setTypeSearch(Sex.valueOf(personDTO.getGenderSearch()));
        person.setBotState(BotState.PROFILE_DONE);
        person.setPageCounter(1);
        persons.add(person);
        log.info("Set to user: " + userId + " from PersonDTO " + person);
    }

    /**
     * Установка поля name и description для данного пользака
     *
     * @param str    Строка, содержащая name и description
     * @param userId Id пользака из телеграмма
     * @param reg    Регулярное выражение для парсинга
     */
    public void setNameAndDescription(String str, Long userId, String reg) {
        Person person = getUsersCurrentPerson(userId);
        String[] params = str.split(reg);
        for (int i = 0; i < params.length; i++) {
            if (i == 0) {
                person.setName(params[0]);
                person.setDescription(new StringBuilder());
            } else {
                if (person.getDescription() == null) {
                    person.setDescription(person.getDescription().append(params[i]));
                } else {
                    person.setDescription(person.getDescription().append(" ").append(params[i]));
                }
            }
        }
        log.info("Set to user: " + userId + " name, descr " + person);
    }

    /**
     * Получение поля name и description для данного пользака
     *
     * @param userId Id пользака из телеграмма
     * @return Склеинная итоговая строка
     */
    public String getNameAndDescription(Long userId) {
        Person result = getUsersCurrentPerson(userId);
        return result.getName() + "\n" + result.getDescription();
    }

    /**
     * Установление нового состояния
     *
     * @param userId   Id пользака из телеграмма
     * @param botState Новое состояние
     */
    public void setNewState(Long userId, BotState botState) {
        for (Person person : persons) {
            if (person.getPersonId().equals(userId)) {
                person.setBotState(botState);
            }
        }
        log.info("Set to user: " + userId + " state - " + botState);
    }

    /**
     * Установление пола
     *
     * @param userId Id пользака из телеграмма
     * @param sex    Устанаваливаемый пол
     */
    public void setNewSex(Long userId, Sex sex) {
        Person person = getUsersCurrentPerson(userId);
        person.setSex(sex);
        log.info("Set to user: " + userId + " Sex - " + sex);
    }

    /**
     * Установление пола для поиска
     *
     * @param userId Id пользака из телеграмма
     * @param sex    Устанаваливаемый пол
     */
    public void setTypeSearch(Long userId, Sex sex) {
        Person person = getUsersCurrentPerson(userId);
        person.setTypeSearch(sex);
        log.info("Set to user: " + userId + " TypeSearch - " + sex);
    }

    /**
     * Получение текущего состояния бота для пользвателя
     *
     * @param userId Id пользака из телеграмма
     * @return Состояние бота для этого пользователя
     */
    public BotState getUsersCurrentBotState(Long userId) {
        for (Person person : persons) {
            if (person.getPersonId().equals(userId)) {
                return person.getBotState();
            }
        }
        return BotState.DEF;
    }

    /**
     * Получение всего Person для данного пользака
     *
     * @param userId Id пользака из телеграмма
     * @return Итоговый Person
     */
    public Person getUsersCurrentPerson(Long userId) {
        for (Person person : persons) {
            if (person.getPersonId().equals(userId)) {
                return person;
            }
        }
        return Person.builder().personId(userId).build();
    }

    /**
     * Проверка на то, есть ли в кеше уже Person с таким userId
     *
     * @param userId Id пользака из телеграмма
     * @return есть ли такой Person
     */
    public boolean containsKey(Long userId) {
        for (Person person : persons) {
            if (person.getPersonId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Установление общего количества страниц в поиске/любимцах
     *
     * @param userId  Id пользака из телеграмма
     * @param counter Кол-во страниц
     */
    public void setPages(Long userId, int counter) {
        Person person = getUsersCurrentPerson(userId);
        person.setPages(counter);
        log.info("Set to user: " + userId + " pages - " + counter);
    }

    /**
     * Получение общего количества страниц в поиске/любимцах
     *
     * @param userId Id пользака из телеграмма
     * @return Кол-во страниц
     */
    public int getPages(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        return person.getPages();
    }

    /**
     * Установление текущего id профиля в поиске
     *
     * @param userId        Id пользака из телеграмма
     * @param likedPersonId Id профиля
     */
    public void setLikedPersonId(Long userId, Long likedPersonId) {
        Person person = getUsersCurrentPerson(userId);
        person.setLikedPersonId(likedPersonId);
        log.info("Set to user: " + userId + " likedPersonId - " + likedPersonId);
    }

    /**
     * Получение текущего id профиля в поиске
     *
     * @param userId Id пользака из телеграмма
     * @return Id профиля
     */
    public Long getLikedPersonId(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        return person.getLikedPersonId();
    }

    /**
     * Сброс общего количества страниц в поиске/любимцах
     *
     * @param userId Id пользака из телеграмма
     */
    public void resetPagesCounter(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        person.setPageCounter(1);
    }

    /**
     * Увелечение на 1 номера страницы на которой находимся
     *
     * @param userId Id пользака из телеграмма
     * @return Номер страницы
     */
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

    /**
     * Уменьшение на 1 номера страницы на которой находимся
     *
     * @param userId Id пользака из телеграмма
     * @return Номер страницы
     */
    public int minusPagesCounter(Long userId) {
        Person person = getUsersCurrentPerson(userId);
        int counter = person.getPageCounter();
        int pages = person.getPages();
        if (counter == 1) {
            person.setPageCounter(pages);
        } else {
            person.setPageCounter(counter - 1);
        }
        int resultCounter = person.getPageCounter();
        log.info("Set to user: " + userId + " pagesCounter - " + resultCounter);
        return resultCounter;
    }
}
