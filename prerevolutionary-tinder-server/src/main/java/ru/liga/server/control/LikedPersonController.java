package ru.liga.server.control;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.liga.server.model.LikedPerson;
import ru.liga.server.service.LikedPersonService;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class LikedPersonController {
    private final LikedPersonService likedPersonService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LikedPerson likePerson(@RequestBody LikedPerson likedPerson) {
        return likedPersonService.likePerson(likedPerson);
    }
}