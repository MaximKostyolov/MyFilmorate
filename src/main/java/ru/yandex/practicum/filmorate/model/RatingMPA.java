package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class RatingMPA {

    private Integer id;

    private String name;

    public RatingMPA(Integer id) {
        this.id = id;
    }

}