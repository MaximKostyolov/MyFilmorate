package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("filmsDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void removeFilm(Film film) {
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public Film create(Film film) {
        film.setId(saveIntoFilms(film));
        saveIntoGenres(film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (updateFilm(film) > 0) {
            updateGenres(film);
            return find(film.getId());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> find(Integer id) {
        String sqlQuery = "select F.*, R.NAME as rating_name, group_concat(L.user_id) as user_id, group_concat(G.genre_id) as genre_id " +
                "from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "left join RATING_MPA as R ON F.RATING_MPA_ID = R.RATING_MPA_ID " +
                "where F.film_id = ? " +
                "group by F.film_id";
        List<Film> filmToList = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id);
        if (!filmToList.isEmpty()) {
            return Optional.of(filmToList.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select F.*, R.NAME as rating_name, group_concat(L.user_id) as user_id, group_concat(G.genre_id) as genre_id " +
                "from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "left join RATING_MPA as R ON F.RATING_MPA_ID = R.RATING_MPA_ID " +
                "group by F.film_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> findMostLikesFilm(Integer count) {
        String sqlQuery = "select F.*, R.NAME as rating_name, count(L.user_id), group_concat(L.user_id) as user_id, " +
                "group_concat(G.genre_id) as genre_id " +
                " from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "left join RATING_MPA as R ON F.RATING_MPA_ID = R.RATING_MPA_ID " +
                "group by F.film_id " +
                "order by count(L.user_id) DESC " +
                "limit(" + count + ")";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Genre> findAllGenres() {
        String sqlQuery = "select * from genre";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> findGenreById(int id) {
        String sqlQuery = "select * from genre where genre_id = ?";
        List<Genre> genreToList = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        if (!genreToList.isEmpty()) {
            return Optional.of(genreToList.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<RatingMPA> findAllMPA() {
        String sqlQuery = "select * from RATING_MPA";
        return  jdbcTemplate.query(sqlQuery, this::mapRowToMPA);
    }

    @Override
    public Optional<RatingMPA> findMPAById(int id) {
        String sqlQuery = "select * from RATING_MPA where RATING_MPA_ID = ?";
        List<RatingMPA> mpaToList = jdbcTemplate.query(sqlQuery, this::mapRowToMPA, id);
        if (!mpaToList.isEmpty()) {
            return Optional.of(mpaToList.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        String sqlQuery = "insert into likes(film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLikeToFilm(Integer filmId, Integer userId) {
        String sqlQuery = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) {
        try {
            List<Integer> genreList = new ArrayList<>();
            String arrayOfGenres = resultSet.getString("genre_id");
            if (arrayOfGenres != null) {
                genreList = UserDbStorage.stringToArray(arrayOfGenres);
            }
            Set<Genre> genreSet = new HashSet<>();
            for (Integer genreId : genreList) {
                genreSet.add(genreFromId(genreId));
            }
            List<Integer> likesUserId = new ArrayList<>();
            String arrayOfUserId = resultSet.getString("user_id");
            if (arrayOfUserId != null) {
                likesUserId = UserDbStorage.stringToArray(arrayOfUserId);
            }
            Film film = Film.builder().
                    id(resultSet.getInt("film_id")).
                    name(resultSet.getString("name")).
                    description(resultSet.getString("description")).
                    releaseDate(resultSet.getDate("release_date").toLocalDate()).
                    duration(resultSet.getLong("duration")).
                    likesUserId(likesUserId).
                    genres(genreSet).
                    mpa(RatingMPA.builder().id(resultSet.getInt("rating_mpa_id")).
                            name(resultSet.getString("rating_name")).
                            build()).
                    build();

            return film;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. Фильм c таким id не найден");
        }

    }

    private Genre genreFromId(Integer genreId) {
        Genre genre = null;
        switch (genreId) {
            case 1:
                genre = new Genre(1, "Комедия");
                break;
            case 2:
                genre = new Genre(2, "Драма");
                break;
            case 3:
                genre = new Genre(3, "Мультфильм");
                break;
            case 4:
                genre = new Genre(4, "Триллер");
                break;
            case 5:
                genre = new Genre(5, "Документальный");
                break;
            case 6:
                genre = new Genre(6, "Боевик");
                break;
        }
        return genre;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) {
        try {
            Genre genre = Genre.builder().
                    id(resultSet.getInt("genre_id")).
                    name(resultSet.getString("name")).
                    build();
            return genre;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. Жанр c таким id не найден");
        }
    }

    private RatingMPA mapRowToMPA(ResultSet resultSet, int rowNum) {
        try {
            RatingMPA mpa = RatingMPA.builder().
                    id(resultSet.getInt("rating_mpa_id")).
                    name(resultSet.getString("name")).
                    build();
            return mpa;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. MPA c таким id не найден");
        }
    }

    private int saveIntoFilms(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    private void saveIntoGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "insert into genre_films(film_id, genre_id) values (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
            }
        }
    }

    private int updateFilm(Film film) {
        String sqlQuery = "update films set name = ?, description = ?, release_date = ?, duration = ?, RATING_MPA_id = ?" +
                " where film_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
    }

    private void updateGenres(Film film) {
        String sqlQuery = "delete from GENRE_FILMS where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        saveIntoGenres(film);
    }

}