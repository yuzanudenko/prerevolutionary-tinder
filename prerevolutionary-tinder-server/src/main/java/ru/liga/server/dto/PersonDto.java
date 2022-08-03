package ru.liga.server.dto;

import lombok.Data;

@Data
public class PersonDto {
    private Long id;
    private String gender;
    private Long personId;
    private String fullName;
    private String description;
    private String genderSearch;
    private String status;
}
