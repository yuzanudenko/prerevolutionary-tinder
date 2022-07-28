package ru.liga.server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "PERSON")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "PERSON_ID")
    private Long personId;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "GENDER_SEARCH")
    private String genderSearch;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LIKED_PERSON",
            joinColumns = {
                    @JoinColumn(name = "MAIN_PERSON_ID", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "LIKED_PERSON_ID", nullable = false, updatable = false)})
    private List<LikedPerson> likedByMe;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "LIKED_PERSON",
            joinColumns = {
                    @JoinColumn(name = "LIKED_PERSON_ID", nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "MAIN_PERSON_ID", nullable = false, updatable = false)})
    private List<LikedPerson> likedMe;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Person person = (Person) o;
        return id != null && Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
