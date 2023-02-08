package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
