package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmService.getAll();
    }

    @PostMapping(value = "/films")
    public Film create(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmService.create(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmService.update(film);
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable int id, HttpServletRequest request){
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (id > 0) {
            return filmService.getById(id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if ((id > 0) && (userId > 0)) {
            filmService.addLike(id, userId);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if ((id > 0) && (userId > 0)) {
            filmService.removeLike(id, userId);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (count > 0) {
            return filmService.getMostLikesFilm(count);
        } else {
            log.info("Некорректный запрос. Количество должно быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Количество должно быть больше 0");
        }
    }

    @GetMapping("/genres")
    public List<Genre> getGenres(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (id > 0) {
            return filmService.getGenreById(id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @GetMapping("/mpa")
    public List<RatingMPA> getMPA(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return filmService.getAllMPA();
    }

    @GetMapping("/mpa/{id}")
    public RatingMPA getMPAById(@PathVariable int id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (id > 0) {
            return filmService.getMPAById(id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

}