package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void removeFilm(Film film);

    Film create(Film film);

    Optional<Film> update(Film film);

    Optional<Film> find(Integer id);

    List<Film> findAll();

    List<Film> findMostLikesFilm(Integer count);

    List<Genre> findAllGenres();

    Optional<Genre> findGenreById(int id);

    List<RatingMPA> findAllMPA();

    Optional<RatingMPA> findMPAById(int id);

    void addLikeToFilm(Integer filmId, Integer userId);

    void removeLikeToFilm(Integer filmId, Integer userId);
}