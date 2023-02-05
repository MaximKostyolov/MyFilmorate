package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component("inMemoryStorage")
public abstract class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> filmStorage = new HashMap<>();
    private Integer filmId = 1;

    @Override
    public void removeFilm(Film film) {
        filmStorage.remove(film.getId());
    }

    @Override
    public Film create(Film film) {
        if (film.getId() == null) {
            film.setId(getFilmId());
            setFilmId(getFilmId()+1);
        }
        if (film.getLikesUserId() == null) {
            film.setLikesUserId(new ArrayList<>());
        }
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (film.getLikesUserId() == null) {
            film.setLikesUserId(new ArrayList<>());
        }
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.remove(film.getId());
            filmStorage.put(film.getId(), film);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> find(Integer id) {
        return Optional.ofNullable(filmStorage.get(id));
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public List<Film> findMostLikesFilm(Integer count) {
        List<Film> sortedLikesFilms = new LinkedList<>();
        Comparator<Film> comparator = (film1, film2) -> {
            int size1 = film1.getLikesUserId().size();
            int size2 = film2.getLikesUserId().size();
            if ((size1 == 0) && (size2 == 0)) {
                return film1.getId() - film2.getId();
            }
            if (size1 == 0) {
                return 1;
            }
            if (size2 == 0) {
                return -1;
            }
            int compareBySize = size1 - size2;
            if (compareBySize != 0) {
                return compareBySize;
            } else {
                return film1.getId() - film2.getId();
            }
        };
        TreeSet<Film> sortedFilms = new TreeSet<>(comparator);
        List<Film> films = findAll();
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