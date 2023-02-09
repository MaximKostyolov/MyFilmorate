package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("usersDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void removeUser(User user) {
        String sqlQuery = "delete from USERS where user_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public User create(User user) {
        user = checkUser(user);
        user.setId(saveIntoUsers(user));
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        user = checkUser(user);
        if (updateUser(user) > 0) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> find(Integer id) {
        String sqlQuery = "select U.USER_ID, U.E_MAIL, U.LOGIN, U.NAME, U.BIRTHDAY, group_concat(F.friend_id) as FRIENDS_ID " +
                "from USERS as U " +
                "left join friendship as F ON f.USER_ID = U.USER_ID " +
                "where U.USER_ID = ? " +
                "group by U.USER_ID";
        List<User> userToList = jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
        if (!userToList.isEmpty()) {
            return Optional.of(userToList.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select U.USER_ID, U.E_MAIL, U.LOGIN, U.NAME, U.BIRTHDAY, group_concat(F.friend_id) as FRIENDS_ID " +
                "from USERS as U " +
                "left join friendship as F ON f.USER_ID = U.USER_ID " +
                "group by U.USER_ID";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addToFriends(Integer id, Integer idFriend) {
        String sqlQuery = "insert into friendship(user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, idFriend);
    }

    @Override
    public void removeFromFriends(Integer id, Integer friendsId) {
        String sqlQuery = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, id, friendsId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) {
        try {
            List<Integer> friendsId = new ArrayList<>();
            String arrayOfFriends = resultSet.getString("FRIENDS_ID");
            if (arrayOfFriends != null) {
                friendsId = stringToArray(arrayOfFriends);
            }
            User user = User.builder()
                    .id(resultSet.getInt("USER_ID"))
                    .email(resultSet.getString("E_MAIL"))
                    .login(resultSet.getString("LOGIN"))
                    .name(resultSet.getString("NAME"))
                    .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                    .friendsId(friendsId)
                    .build();
            return user;
        } catch (EmptyResultDataAccessException | SQLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Некорректный запрос. Пользователь c таким id не найден");
        }
    }


    public static List<Integer> stringToArray(String arrayOfFriends) {
        List<Integer> listIds = new ArrayList<>();
        String[] arraySplit = arrayOfFriends.split(",");
        for (int i = 0; i < arraySplit.length; i++) {
            listIds.add(Integer.parseInt(arraySplit[i]));
        }
        return listIds;
    }

    private User checkUser(User user) {
        if (user.getFriendsId() == null) {
            user.setFriendsId(new ArrayList<>());
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return user;
    }

    private int saveIntoUsers(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("user_id");
        return simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
    }

    private int updateUser(User user) {
        String sqlQuery = "update USERS set e_mail = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        int updateCount = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        return updateCount;
    }

}