package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> filmStorage = new HashMap<>();

    @Override
    public void removeFilm(Film film) {
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.remove(film.getId());
        }
    }

    @Override
    public Film create(Film film) {
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film){
        if (filmStorage.containsKey(film.getId())) {
            filmStorage.remove(film.getId());
            filmStorage.put(film.getId(), film);
            return film;
        } else {
            return null;
        }
    }

    @Override
    public Film getFilm(Integer id) {
        return filmStorage.get(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());
    }

}