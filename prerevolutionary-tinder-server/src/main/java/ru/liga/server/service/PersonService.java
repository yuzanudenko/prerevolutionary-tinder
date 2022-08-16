package ru.liga.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.liga.server.dto.PersonDto;
import ru.liga.server.dto.mapping.PersonMapping;
import ru.liga.server.model.Person;
import ru.liga.server.repository.PersonRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapping personMapping;

    /**
     * Поиск данных всех пользователей
     *
     * @return Список данных пользователей
     */
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    /**
     * Обновление данных пользователя
     *
     * @param person Данные пользователя
     */
    public void save(Person person) {
        personRepository.save(person);
    }

    /**
     * Создание нового пользователя
     *
     * @param person Данные пользователя
     * @return Данные пользователя
     */
    public Person create(Person person) {
        return personRepository.saveAndFlush(person);
    }

    /**
     * Поиск данных пользователя по идентификатору
     *
     * @param personId Идентификатор пользователя
     * @return Данные пользователя
     */
    public Person findByPersonId(Long personId) {
        return personRepository.findByPersonId(personId);
    }

    /**
     * Поиск данных всех пользователей подходящих под критерии поиска
     *
     * @param personId Идентификатор текущего пользователя
     * @return Список данных пользователей
     */
    public List<Person> findAllSuitablePersons(Long personId) {
        int count = this.getSuitablePersonsCount(personId);
        Pageable pageable = PageRequest.ofSize(count > 0 ? count : 1).withSort(Sort.by("id").ascending());
        return personRepository.findSuitablePersons(personId, pageable).getContent();
    }

    /**
     * Поиск данных пользователя подходящего под критерии поиска
     * Список полходящих пользователей разивается на "страницы" (одна запись = одна страница)
     *
     * @param personId Идентификатор текущего пользователя
     * @param page     Порядковый номер пользователя
     * @return Данные пользователя
     */
    public Person findSuitablePerson(Long personId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 1, Sort.by("id").ascending());
        Page<Person> statePage = personRepository.findSuitablePersons(personId, pageable);
        Person person = statePage.getContent().get(0);

        log.info("Suitable person № {}: {}", page, person);

        return person;
    }

    /**
     * Поиск количества пользователей подходящих под критерии поиска
     *
     * @param personId Идентификатор текущего пользователя
     * @return Количество польщователей
     */
    public int getSuitablePersonsCount(Long personId) {
        int count = personRepository.getSuitablePersonsCount(personId);

        log.info("Count suitable persons: {}", count);

        return count;
    }

    /**
     * Поиск списка "любимых" пользователей
     *
     * @param personId Идентификатор пользователя
     * @return Список пользователей
     */
    public List<PersonDto> findAllFavoritePersons(Long personId) {
        int count = this.getFavoritePersonsCount(personId);
        Pageable pageable = PageRequest.ofSize(count > 0 ? count : 1).withSort(Sort.by("id").ascending());
        Person mainPerson = personRepository.findByPersonId(personId);
        return personMapping.createModelList(personRepository.findLikedPersons(personId, pageable).getContent(), mainPerson.getId());
    }

    /**
     * Поиск данных "любимого" пользователя
     * Список "любимых" пользователей разивается на "страницы" (одна запись = одна страница)
     *
     * @param personId Идентификатор текущего пользователя
     * @param page     Порядковый номер пользователя
     * @return Данные пользователя
     */
    public PersonDto findFavoritePerson(Long personId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 1, Sort.by("id").ascending());
        Page<Person> statePage = personRepository.findLikedPersons(personId, pageable);
        Person mainPerson = personRepository.findByPersonId(personId);
        return personMapping.createModel(statePage.getContent().get(0), mainPerson.getId());
    }

    /**
     * Получить количествщ "любимых" пользователей
     *
     * @param personId Идентификатор текущего пользователя
     * @return Количество польщователей
     */
    public int getFavoritePersonsCount(Long personId) {
        int count = personRepository.getLikedPersonsCount(personId);

        log.info("Count suitable persons: {}", count);

        return count;
    }
}
