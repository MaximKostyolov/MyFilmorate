package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private Integer filmId = 1;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Film> findAll() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Ошибка валидации фильма. Дата релиза не может быть раньше 28 декабря 1985 года");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка валидации фильма. " +
                    "Дата релиза не может быть раньше 28 декабря 1985 года");
        }
        if (film.getId() == null) {
            film.setId(getFilmId());
            setFilmId(getFilmId()+1);
        }
        if (film.getLikesUserId() == null) {
            film.setLikesUserId(new ArrayList<>());
        }
        filmStorage.create(film);
        return film;
    }

    public Film updateFilm(Film film) {
        boolean isContain = false;
        if (film.getLikesUserId() == null) {
            film.setLikesUserId(new ArrayList<>());
        }
        if (!filmStorage.getAllFilms().isEmpty()) {
            Film filmUpdate = filmStorage.update(film);
            if (filmUpdate != null) {
                isContain = true;
            }
        }  else {
            log.info("Фильмы отсутствуют в базе");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильмы отсутствуют в базе");
        }
        if (!isContain) {
            log.info("Некорректный запрос. Фильм c таким id не найден");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Фильм c таким id не найден");
        }
        return film;
    }

    public Film findFilmById(Integer id) {
        if (id > 0) {
            Film film = filmStorage.getFilm(id);
            if (film != null) {
                return film;
            } else {
                log.info("Фильм c таким id не найден");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Фильм c таким id не найден");
            }
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    public void addLike(Integer filmId, Integer userId) {
        if ((filmId > 0) && (userId > 0)) {
            Film film = filmStorage.getFilm(filmId);
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
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    public void removeLike(Integer filmId, Integer userId) {
        if ((filmId > 0) && (userId > 0)) {
            Film film = filmStorage.getFilm(filmId);
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
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    public List<Film> findMostLikesFilm(Integer count) {
        List<Film> sortedLikesFilms = new LinkedList<>();
        Comparator<Film> comparator = (film1, film2) -> {
            int size1 = film1.getLikesUserId().size();
            int size2 = film2.getLikesUserId().size();
            if (size1 == 0) {
                return 1;
            }
            if (size2 == 0) {
                return -1;
            }
            if ((size1 == 0) && (size2 == 0)) {
                return film1.getId() - film2.getId();
            }
            int compareBySize = size1 - size2;
            if (compareBySize != 0) {
                return compareBySize;
            } else {
                return film1.getId() - film2.getId();
            }
        };
        TreeSet<Film> sortedFilms = new TreeSet<Film>(comparator);
        List<Film> films = filmStorage.getAllFilms();
        sortedFilms.addAll(films);
        if (sortedFilms.size() < count) {
            count = sortedFilms.size();
        }
        for (int i = 0; i < count; i++) {
            if (!sortedFilms.isEmpty()) {
                Film film = sortedFilms.first();
                sortedLikesFilms.add(film);
                films.remove(film);
                sortedFilms.clear();
                sortedFilms.addAll(films);
            }
        }
        return sortedLikesFilms;
    }

    private Integer getFilmId() {
        return filmId;
    }

    private void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

}