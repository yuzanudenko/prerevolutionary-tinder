package ru.liga.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.liga.server.model.LikedPerson;
import ru.liga.server.repository.LikedPersonRepository;

@Service
@RequiredArgsConstructor
public class LikedPersonService {
    private final LikedPersonRepository likedPersonRepository;

    public LikedPerson likePerson(LikedPerson likedPerson) {
        LikedPerson likedPersonExists = likedPersonRepository
                .getByMainIdAndLikedId(likedPerson.getMainId(), likedPerson.getLikedId());
        if (likedPersonExists == null) {
            return likedPersonRepository.saveAndFlush(likedPerson);
        }
        return likedPersonExists;
    }
}
