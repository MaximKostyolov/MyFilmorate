package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    @Autowired
    public UserService(@Qualifier("usersDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        user = userStorage.update(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Некорректный запрос. Пользователь c таким id не найден"));
        return user;
    }

    public List<User> getFriends(Integer id) {
        List<User> usersFriends = new ArrayList<>();
        User user = userStorage.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Некорректный запрос. Пользователь c таким id не найден"));
        if (!user.getFriendsId().isEmpty()) {
            List<Integer> friendsId = user.getFriendsId();
            for (Integer friendId : friendsId) {
                usersFriends.add(getById(friendId));
            }
        } else {
            log.info("Список друзей пуст");
        }

        return usersFriends;
    }

    public void addFriends(Integer id, Integer friendId) {
        userStorage.addToFriends(id, friendId);

    }

    public void removeFromFriends(Integer id, Integer friendId) {
        userStorage.removeFromFriends(id, friendId);
    }

    public User getById(Integer id) {
        return userStorage.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Некорректный запрос. Пользователь c таким id не найден"));
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        User user1 = getById(id);
        User user2 = getById(otherId);
        if ((user1 != null) || (user2 != null)) {
            if ((!user1.getFriendsId().isEmpty()) || (!user2.getFriendsId().isEmpty())) {
                for (Integer friendId : user1.getFriendsId()) {
                    if (user2.getFriendsId().contains(friendId)) {
                        commonFriends.add(getById(friendId));
                    }
                }
            } else {
                log.info(user1.getName() + " или " + user2.getName() + " не имеют друзей");
            }
        } else {
            log.info("Пользователь не существует");
        }

        return commonFriends;
    }

}