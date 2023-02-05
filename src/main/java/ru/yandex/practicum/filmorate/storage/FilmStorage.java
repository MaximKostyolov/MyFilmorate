package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void removeFilm(Film film);

    Film create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> find(Integer id);

    List<Film> findAll();

    List<Film> findMostLikesFilm(Integer count);

    HashMap<Integer, String> findAllGenres();

    HashMap<Integer, String> findGenreById(int id);

    HashMap<Integer, String> findAllMPA();

    HashMap<Integer, String> findMPAById(int id);

}