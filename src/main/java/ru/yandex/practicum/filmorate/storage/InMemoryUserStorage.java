package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("inMemoryStorage")
public abstract class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> userStorage = new HashMap<>();
    private Integer userId = 1;

    @Override
    public void removeUser(User user) {
        if (userStorage.containsKey(user.getId())) {
            userStorage.remove(user.getId());
        }
    }

    @Override
    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == null) {
            user.setId(getUserId());
            setUserId(getUserId()+1);
        }
        if (user.getFriendsId() == null) {
            user.setFriendsId(new ArrayList<>());
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        if (user.getFriendsId() == null) {
            user.setFriendsId(new ArrayList<>());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (userStorage.containsKey(user.getId())) {
            userStorage.remove(user.getId());
            userStorage.put(user.getId(), user);
            return Optional.of(user);
        } else {
            return null;
        }
    }

    @Override
    public Optional<User> find(Integer id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }

    private Integer getUserId() {
        return userId;
    }

    private void setUserId(Integer userId) {
        this.userId = userId;
    }

}