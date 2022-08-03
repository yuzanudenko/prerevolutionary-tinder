package ru.liga.server.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "LIKED_PERSON")
public class LikedPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MAIN_PERSON_ID")
    private Long mainPersonId;

    @Column(name = "LIKED_PERSON_ID")
    private Long likedPersonId;

    public LikedPerson(Long mainPersonId, Long likedPersonId) {
        this.mainPersonId = mainPersonId;
        this.likedPersonId = likedPersonId;
    }
}
