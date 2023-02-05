package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("filmsDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private Integer filmId = 1;


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void removeFilm(Film film) {
        String sqlQuery = "delete from films where id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public Film create(Film film) {
        film = checkFilm(film);
        saveIntoFilms(film);
        saveIntoLikes(film);
        saveIntoGenres(film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        film = checkFilm(film);
        return updateFilm(film);
    }

    @Override
    public Optional<Film> find(Integer id) {
        String sqlQuery = "select F.*, group_contact(L.user_id), group_contact(G.genre_id) " +
                "from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "where film_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id));
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "select F.*, group_contact(L.user_id), group_contact(G.genre_id) " +
                "from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "group by F.film_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public List<Film> findMostLikesFilm(Integer count) {
        String sqlQuery = "select F.*, count(L.user_id) " +
                " from films as F " +
                "left join likes as L ON F.film_id = L.film_id " +
                "left join genre_films as G ON F.film_id = G.film_id " +
                "group by F.film_id " +
                "order by count(L.user_id) DESC " +
                "limit(count)";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public HashMap<Integer, String> findAllGenres() {
        HashMap<Integer, String> allGenres = new HashMap<>();
        SqlRowSet allGenresRows = jdbcTemplate.queryForRowSet("select * " +
                "from genre");
        SqlRowSetMetaData allGenresRowsmd = allGenresRows.getMetaData();
        if (allGenresRows.next()) {
            for (int i = 0; i < allGenresRowsmd.getColumnCount(); i++) {
                allGenres.put(allGenresRows.getInt("genre_id"), allGenresRows.getString("name"));
            }
        }
        return allGenres;
    }

    @Override
    public HashMap<Integer, String> findGenreById(int id) {
        HashMap<Integer, String> genreById = new HashMap<>();
        SqlRowSet genreByIdRow = jdbcTemplate.queryForRowSet("select * " +
                "from genre " +
                "where film_id = ?");
        if (genreByIdRow.next()) {
            genreById.put(genreByIdRow.getInt("genre_id"), genreByIdRow.getString("name"));
        }
        return genreById;
    }

    @Override
    public HashMap<Integer, String> findAllMPA() {
        HashMap<Integer, String> allMPA = new HashMap<>();
        SqlRowSet allMPARow = jdbcTemplate.queryForRowSet("select * " +
                "from rating_mpa");
        SqlRowSetMetaData allMPARowmd = allMPARow.getMetaData();
        if (allMPARow.next()) {
            for (int i = 0; i < allMPARowmd.getColumnCount(); i++) {
                allMPA.put(allMPARow.getInt("rating_MPA_id"), allMPARow.getString("name"));
            }
        }
        return allMPA;
    }

    @Override
    public HashMap<Integer, String> findMPAById(int id) {
        HashMap<Integer, String> genreById = new HashMap<>();
        SqlRowSet genreByIdRow = jdbcTemplate.queryForRowSet("select * " +
                "from genre " +
                "where film_id = ?");
        if (genreByIdRow.next()) {
            genreById.put(genreByIdRow.getInt("genre_id"), genreByIdRow.getString("name"));
        }
        return genreById;
    }

    private Film checkFilm(Film film) {
        if (film.getId() == null) {
            film.setId(getFilmId());
            setFilmId(getFilmId()+1);
        }
        if (film.getLikesUserId() == null) {
            film.setLikesUserId(new ArrayList<>());
        }
        return film;
    }

    private Film mapRowToFilm(ResultSet resultSet, int i) {
        try {
            List<Integer> genreId = new ArrayList<>();
            Array arrayOfGenres = null;
            arrayOfGenres = resultSet.getArray("G.genre_id");
            genreId = (List<Integer>) arrayOfGenres.getArray();
            List<Integer> likesUserId = new ArrayList<>();
            Array arrayOfUserId = resultSet.getArray("L.user_id");
            likesUserId = (List<Integer>) arrayOfGenres.getArray();
            Film film = new Film(resultSet.getInt("F.film_id"),
                    resultSet.getString("F.name"),
                    resultSet.getString("F.description"),
                    resultSet.getDate("F.release_date").toLocalDate(),
                    resultSet.getLong("F.duration"),
                    likesUserId,
                    genreId,
                    resultSet.getInt("F.rating_mpa_id"));

            return film;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveIntoFilms(Film film) {
        String sqlQuery = "insert into films(name, description, release_date, duration, rating_MPA_id)" +
                " values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRatingMPAId());
    }

    private void saveIntoLikes(Film film) {
        if (!film.getLikesUserId().isEmpty()) {
            for (Integer userId : film.getLikesUserId()) {
                String sqlQuery = "insert into likes(film_id, user_id) values (?, ?)";
                jdbcTemplate.update(sqlQuery, film.getId(), userId);
            }
        }
    }

    private void saveIntoGenres(Film film) {
        for (Integer genreId : film.getGenreId()) {
            String sqlQuery = "insert into genre_films(film_id, genre_id) values (?, ?)";
            jdbcTemplate.update(sqlQuery, film.getId(), genreId);
        }
    }

    private Optional<Film> updateFilm(Film film) {
        String sqlQuery = "update films set name = ?, description = ?, releaseDate = ?, duration = ?, rating_mpa_id = ?" +
                " where id = ?";
        int existense = jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getRatingMPAId());
        if (existense > 0) {
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    private Integer getFilmId() {
        return filmId;
    }

    private void setFilmId(Integer filmId) {
        this.filmId = filmId;
    }

}