package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private int idUser = 0;
    private final List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> findAll(HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        return users;
    }

    @PostMapping(value = "/users")
    public User create(@Valid @RequestBody User user, HttpServletRequest request) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        User userForList;
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            setIdUser(getIdUser() + 1);
            user.setId(getIdUser());
        }
        userForList = user;
        users.add(userForList);
        return userForList;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: '{} {}', Строка параметров запроса: '{}'",
                request.getMethod(), request.getRequestURI(), request.getQueryString());
        boolean isContain = false;
        if (!users.isEmpty()) {
            for (User userFromList : users) {
                if (userFromList.getId() == user.getId()) {
                    users.remove(userFromList);
                    users.add(user);
                    isContain = true;
                    break;
                }
            } if (!isContain) {
                log.info("Некорректный запрос. User c таким id не найден");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. User c таким id не найден");
            }
        } else {
            log.info("User-ы отсутствуют в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User-ы отсутствуют в базе данных");
        }
        return user;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

}