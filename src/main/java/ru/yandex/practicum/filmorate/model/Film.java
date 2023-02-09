package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
@Builder
public class Film {

    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @NotBlank
    @Size(max=200)
    private String description;

    @Positive
    private Long duration;

    private Integer rate;

    private RatingMPA mpa;

    private Set<Genre> genres;

    @Builder.Default
    private List<Integer> likesUserId = new ArrayList<>();

    public Map<String,Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        values.put("description", description);
        values.put("release_date", releaseDate);
        values.put("duration", duration);
        values.put("rating_mpa_id", mpa.getId());
        return values;
    }
}