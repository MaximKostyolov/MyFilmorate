package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> getAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка валидации фильма. Дата релиза не может быть раньше 28 декабря 1985 года");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка валидации фильма. " +
                    "Дата релиза не может быть раньше 28 декабря 1985 года");
        }
        filmStorage.create(film);

        return film;
    }

    public Film update(Film film) {
        if (!filmStorage.findAll().isEmpty()) {
            Film filmUpdate = filmStorage.update(film).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. Фильм c таким id не найден"));
        }  else {
            log.info("Фильмы отсутствуют в базе");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильмы отсутствуют в базе");
        }

        return film;
    }

    public Film getById(Integer id) {
        return filmStorage.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Некорректный запрос. Фильм c таким id не найден"));
    }

    public void addLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        List<Integer> likesUserId = new ArrayList<>();
        if (!film.getLikesUserId().isEmpty()) {
            likesUserId = film.getLikesUserId();
        }
        if (!likesUserId.contains(userId)) {
            likesUserId.add(userId);
            film.setLikesUserId(likesUserId);
            filmStorage.update(film);
        } else {
            log.info("Пользователь уже оценил этот фильм");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = getById(filmId);
        if (!film.getLikesUserId().isEmpty()) {
            List<Integer> likesUserId = film.getLikesUserId();
            if (likesUserId.contains(userId)) {
                likesUserId.remove(userId);
                film.setLikesUserId(likesUserId);
                filmStorage.update(film);
            } else {
                log.info("Пользователь не ставил лайк фильму");
            }
        } else {
            log.info("У фильма нет лайков");
        }
    }

    public List<Film> getMostLikesFilm(Integer count) {
        return filmStorage.findMostLikesFilm(count);
    }

}