package ru.yandex.practicum.filmorate.validationTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

public class ValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void createFilmWithFailName() {
        Film film = Film.builder()
                .name("       ")
                .description("Description")
                .releaseDate(LocalDate.of(2020, 5, 9))
                .duration(120L)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    public void createFilmWithFailDescription() {
        Film film = Film.builder()
                .name("Name")
                .description("Description ajvnakdj;fv;kjn;kfjvnkjsaVNKADVNKJASDNVJAFBNKJADFBKAN  NA;kjsnvs;n ; aana " +
                        ";616851+;akjsbna;jkvn;jkanvjk;an;jkvnaj;kvn ajk;vnjk;a;nvjakvnajk;vnjanvak;anv;akjvkanja;kn" +
                        "sa;lbkna;jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjnqvvvvvvvv" +
                        "a;;;;;;;;;;;;;;;;;;;;vnnnnnnnnnnnnq;vnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnvvvvvvnv;n")
                .releaseDate(LocalDate.of(2020, 5, 9))
                .duration(120L)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    public void createFilmWithFailDuration() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(-120L)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        ConstraintViolation<Film> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    public void createUserWithFailEmail() {
        User user = User.builder()
                .login("Login")
                .name("Name")
                .email("email.ru")
                .birthday(LocalDate.of(1994, 12, 8))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("email", violation.getPropertyPath().toString());
    }

    @Test
    public void createUserWithFailLogin() {
        User user = User.builder()
                .login(null)
                .name("Name")
                .email("email@mail.ru")
                .birthday(LocalDate.of(1994, 12, 8))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("login", violation.getPropertyPath().toString());
    }

    @Test
    public void createUserWithFailBirthday() {
        User user = User.builder()
                .login("Login")
                .name("Name")
                .email("email@mail.ru")
                .birthday(LocalDate.of(2024, 12, 8))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        ConstraintViolation<User> violation = violations.stream().findFirst().orElseThrow(() ->
                new RuntimeException("Отсутствует ошибка валидации"));
        Assertions.assertEquals(1, violations.size());
        Assertions.assertEquals("birthday", violation.getPropertyPath().toString());
    }

}
