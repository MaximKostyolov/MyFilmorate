package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;


public interface UserStorage {

    void removeUser(User user);

    User create(User user);

    User update(User user);

    User getUser(Integer id);

    List<User> getAllUsers();

}
