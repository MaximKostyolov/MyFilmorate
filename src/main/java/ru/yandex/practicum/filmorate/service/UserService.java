package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private Integer userId = 1;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
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
        userStorage.create(user);
        return user;
    }

    public User updateUser(User user) {
        boolean isContain = false;
        if (user.getFriendsId() == null) {
            user.setFriendsId(new ArrayList<>());
        }
        if (!userStorage.getAllUsers().isEmpty()) {
            User returnUser = userStorage.update(user);
            if (returnUser != null) {
                isContain = true;
            }
        } else {
            log.info("Пользователи отсутствуют в базе данных");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователи отсутствуют в базе данных");
        }
        if (!isContain) {
            log.info("Некорректный запрос. Пользователь c таким id не найден");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Пользователь c таким id не найден");
        }
        return user;
    }

    public List<User> getUsersFriends(Integer id) {
        List<User> usersFriends = new ArrayList<>();
        if (id > 0) {
            if (userStorage.getUser(id) != null) {
                if (!userStorage.getUser(id).getFriendsId().isEmpty()) {
                    List<Integer> friendsId = userStorage.getUser(id).getFriendsId();
                    for (Integer friendId : friendsId) {
                        usersFriends.add(userStorage.getUser(friendId));
                    }
                } else {
                    log.info("Список друзей пуст");
                }
            } else {
                log.info("Пользователь не найден");
            }
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
        return usersFriends;
    }

    public void addFriends(Integer id, Integer friendId) {
        if ((id > 0) && (friendId > 0)) {
            addFriendToUser(userStorage.getUser(id), friendId);
            addFriendToUser(userStorage.getUser(friendId), id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    public void removeFromFriends(Integer id, Integer friendId) {
        if ((id > 0) && (friendId > 0)) {
            removeFriend(userStorage.getUser(id), friendId);
            removeFriend(userStorage.getUser(id), id);
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    public User findUserById(Integer id) {
        if (id > 0) {
            User user = userStorage.getUser(id);
            if (user != null) {
                return user;
            } else {
                log.info("Пользователь с таким id не найден");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким id не найден");
            }
        } else {
            log.info("Некорректный запрос. Id должен быть больше 0");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный запрос. Id должен быть больше 0");
        }
    }

    private void addFriendToUser(User user, Integer idFriend) {
        if (user != null) {
            List<Integer> friendsIdUser = user.getFriendsId();
            if (!friendsIdUser.isEmpty()) {
                if (!friendsIdUser.contains(idFriend)) {
                    friendsIdUser.add(idFriend);
                    user.setFriendsId(friendsIdUser);
                } else {
                    log.info(user.getName() + " и " + userStorage.getUser(idFriend).getName() + "уже в друзьях");
                }
            } else {
                friendsIdUser.add(idFriend);
                user.setFriendsId(friendsIdUser);
            }
            userStorage.update(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким id не найден");
        }
    }

    private void removeFriend(User user, Integer id) {
        if (!user.getFriendsId().isEmpty()) {
            List<Integer> friendsIdUser = user.getFriendsId();
            if (friendsIdUser.contains(id)) {
                friendsIdUser.remove(id);
                user.setFriendsId(friendsIdUser);
                userStorage.update(user);
            } else {
                log.info(user.getName() + " и " + userStorage.getUser(id).getName() + "не были в друзьях");
            }
        } else {
            log.info("У пользователя " + user.getName() + "нет друзей");
        }
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        User user1 = userStorage.getUser(id);
        User user2 = userStorage.getUser(otherId);
        if ((user1 != null) || (user2 != null)) {
            if ((!user1.getFriendsId().isEmpty()) || (!user2.getFriendsId().isEmpty())) {
                for (Integer friendId : user1.getFriendsId()) {
                    if (user2.getFriendsId().contains(friendId)) {
                        commonFriends.add(userStorage.getUser(friendId));
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

    private Integer getUserId() {
        return userId;
    }

    private void setUserId(Integer userId) {
        this.userId = userId;
    }

}