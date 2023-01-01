package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> userStorage = new HashMap<>();

    @Override
    public void removeUser(User user) {
        if (userStorage.containsKey(user.getId())) {
            userStorage.remove(user.getId());
        }
    }

    @Override
    public User create(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user){
        if (userStorage.containsKey(user.getId())) {
            userStorage.remove(user.getId());
            userStorage.put(user.getId(), user);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public User getUser(Integer id) {
        return userStorage.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

}