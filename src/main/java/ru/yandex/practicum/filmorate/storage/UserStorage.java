package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface UserStorage {

    void removeUser(User user);

    User create(User user);

    Optional<User> update(User user);

    Optional<User> find(Integer id);

    List<User> findAll();

}
