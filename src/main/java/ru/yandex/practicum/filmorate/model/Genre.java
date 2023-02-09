package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class Genre {

    private Integer id;

    private String name;

    public Genre(Integer id) {
        this.id = id;
    }

}