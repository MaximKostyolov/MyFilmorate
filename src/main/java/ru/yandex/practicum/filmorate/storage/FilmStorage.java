package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


public interface FilmStorage {

    void removeFilm(Film film);

    Film create(Film film);

    Film update(Film film);

    Film getFilm(Integer id);

    List<Film> getAllFilms();

}
