package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private int idFilm = 0;
    private final List<Film> films = new ArrayList<>();

    @GetMapping("/films")
    public List<Film> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return films;
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        Film filmForList;
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка валидации фильма. Дата релиза не может быть раньше 28 декабря 1985 года");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка валидации фильма. " +
                    "Дата релиза не может быть раньше 28 декабря 1985 года");
        }
        if (film.getId() == 0) {
            setIdFilm(getIdFilm() + 1);
            film.setId(getIdFilm());
        }
        filmForList = film;
        films.add(filmForList);
        return filmForList;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        boolean isContain = false;
        if (!films.isEmpty()) {
            for (Film filmFromList : films) {
                if (filmFromList.getId() == film.getId()) {
                    films.remove(filmFromList);
                    films.add(film);
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                log.info("Некорректный запрос. Фильм c таким id не найден");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Фильм c таким id не найден");
            }
        } else {
            log.info("Фильмы отсутствуют в базе");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильмы отсутствуют в базе");
        }
        return film;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }

}