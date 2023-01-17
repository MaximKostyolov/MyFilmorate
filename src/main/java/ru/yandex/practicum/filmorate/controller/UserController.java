package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userService.getAll();
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userService.create(user);
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userService.update(user);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if (id > 0) {
            return userService.getById(id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriends(@PathVariable Integer id, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return userService.getFriends(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable Integer id, @PathVariable Integer friendId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if ((id > 0) && (friendId > 0)) {
            userService.addFriends(id, friendId);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable Integer id, @PathVariable Integer friendId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if ((id > 0) && (friendId > 0)) {
            userService.removeFromFriends(id, friendId);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        if ((id > 0) && (otherId > 0)) {
            return userService.getCommonFriends(id, otherId);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

}