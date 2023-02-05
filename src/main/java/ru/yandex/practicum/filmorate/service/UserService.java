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
        if (!userStorage.findAll().isEmpty()) {
            user = userStorage.update(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. Пользователь c таким id не найден"));
        } else {
            log.info("Пользователи отсутствуют в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователи отсутствуют в базе данных");
        }

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
        User user = userStorage.find(friendId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Некорректный запрос. Пользователь c таким id не найден"));
        //addFriendToUser(user1, friendId);
        addFriendToUser(user, id);
    }

    public void removeFromFriends(Integer id, Integer friendId) {
        User user = getById(id);
        removeFriend(user, friendId);
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

    private void addFriendToUser(User user, Integer idFriend) {
        List<Integer> friendsIdUser = user.getFriendsId();
        if (!friendsIdUser.isEmpty()) {
            if (!friendsIdUser.contains(idFriend)) {
                userStorage.addToFriends(user, idFriend);
            } else {
                log.info(user.getName() + " и " + getById(idFriend).getName() + "уже в друзьях");
            }
        } else {
            userStorage.addToFriends(user, idFriend);
        }

    }

    private void removeFriend(User user, Integer id) {
        if (!user.getFriendsId().isEmpty()) {
            List<Integer> friendsIdUser = user.getFriendsId();
            if (friendsIdUser.contains(id)) {
                userStorage.removeFromFriends(user, id);
            } else {
                log.info(user.getName() + " и " + getById(id).getName() + "не были в друзьях");
            }
        } else {
            log.info("У пользователя " + user.getName() + "нет друзей");
        }
    }

}