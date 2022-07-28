package ru.liga.server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "PERSON")
public class LikedPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "GENDER_SEARCH")
    private String genderSearch;
}
