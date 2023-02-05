package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("usersDbStorage")
@Slf4j
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    /*public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }*/

    @Override
    public void removeUser(User user) {
        String sqlQuery = "delete from users where id = ?";
        jdbcTemplate.update(sqlQuery, user.getId());
    }

    @Override
    public User create(User user) {
        user = checkUser(user);
        saveIntoUsers(user);
        saveIntoFriendship(user);

        return user;
    }

    @Override
    public Optional<User> update(User user) {
        user = checkUser(user);
        return updateUser(user);
    }

    @Override
    public Optional<User> find(Integer id) {
        String sqlQuery = "select U.*, group_contact(F.friends_id) " +
                "from users as U " +
                "left join friendship as F ON f.user_id = U.user_id" +
                "where user_id = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
    }

    @Override
    public List<User> findAll() {
        String sqlQuery = "select U.*, group_contact(F.friends_id) " +
                "from users as U " +
                "left join friendship as F ON f.user_id = U.user_id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void addToFriends(User user, Integer idFriend) {
        String sqlQuery = "insert into friendship(user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), idFriend);
    }

    @Override
    public void removeFromFriends(User user, Integer id) {
        String sqlQuery = "delete from friendship where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlQuery, user.getId(), id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        List<Integer> friendsId = new ArrayList<>();
        Array arrayOfFriends = resultSet.getArray("F.friend_id");
        friendsId = (List<Integer>) arrayOfFriends.getArray();
        User user = new User(resultSet.getInt("U.user_id"),
                resultSet.getString("U.e_mail"),
                resultSet.getString("U.login"),
                resultSet.getString("U.name"),
                resultSet.getDate("U.birthday").toLocalDate(),
                friendsId);

        return user;
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

    private void saveIntoUsers(User user) {
        String sqlQuery = "insert into users(e_mail, login, name, birthday) values (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    private void saveIntoFriendship(User user) {
        if (!user.getFriendsId().isEmpty()) {
            for (Integer friendsId : user.getFriendsId()) {
                String sqlQuery = "insert into friendship(user_id, friend_id) values (?, ?)";
                jdbcTemplate.update(sqlQuery, user.getId(), friendsId);
            }
        }
    }

    private Optional<User> updateUser(User user) {
        String sqlQuery = "update users set e_mail = ?, login = ?, name = ?, birthday = ? where id = ?";
        int existense = jdbcTemplate.update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        if (existense > 0) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

}