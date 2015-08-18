package ru.mashintsev.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.mashintsev.domain.User;
import ru.mashintsev.domain.UserStatistic;
import ru.mashintsev.exceptions.NotFoundUserException;

import java.sql.JDBCType;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
@Service
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String getNewUserId() {

        String randomId = null;
        do {
            randomId = UUID.randomUUID().toString();
        } while (exists(randomId));

        return randomId;
    }

    public boolean exists(String id) {
        Integer exists = jdbcTemplate.query("select 1 from user where id = ?",
                new Object[]{id},
                rs -> {
                    return rs.next() ? rs.getInt(1) : null;
                });
        return exists != null;
    }

    public User save(User user) {
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(getNewUserId());
        }

        jdbcTemplate.update("insert into user(id, photo_uri, login, email, status) values(?,?,?,?,?)",
                user.getId(), user.getPhotoUri(), user.getLogin(), user.getEmail(), user.getStatus());

        return user;
    }

    /**
     * @param userId
     * @return
     */
    public User findById(String userId) {
        return jdbcTemplate.query(
                "SELECT id, photo_uri, login, email, status, status_timestamp FROM user WHERE id = ?", new Object[]{userId},
                rs -> {
                    return rs.next() ? new User(rs.getString("id"), rs.getString("photo_uri"), rs.getString("login"),
                            rs.getString("email"), rs.getString("status"), rs.getDate("status_timestamp")) : null;
                }
        );
    }

    public String updateStatus(String userId, String status) throws NotFoundUserException {

        String oldStatus = jdbcTemplate.query("select status from user where id = ?", new Object[]{userId},
                rs -> {
                    return rs.next() ? rs.getString(1) : null;
                }
        );
        int upd = jdbcTemplate.update("update user set status = ?, status_timestamp = ? where id = ?",
                new Object[]{status, new Date(), userId},
                new int[]{JDBCType.VARCHAR.getVendorTypeNumber(), JDBCType.TIMESTAMP.getVendorTypeNumber(),
                        JDBCType.VARCHAR.getVendorTypeNumber()}
        );

        if (upd < 1) {
            throw new NotFoundUserException("No user with id=" + userId + " to update");
        }

        return oldStatus;
    }

    public List<UserStatistic> getStatistics(String status, Date timestamp) {
        StringBuilder sql = new StringBuilder("SELECT photo_uri, status FROM user");

        boolean whereAdded = false;
        List<Object> args = new ArrayList<>(2);
        if (!StringUtils.isEmpty(status)) {
            sql.append(" WHERE ");
            whereAdded = true;
            sql.append(" status = ? ");
            args.add(status);
        }
        if (timestamp != null) {
            if (!whereAdded)
                sql.append(" WHERE ");
            else
                sql.append(" and ");

            sql.append(" status_timestamp >= ?");
            args.add(timestamp);
        }

        return jdbcTemplate.query(
                sql.toString(), args.toArray(new Object[args.size()]),
                (rs, rowNum) -> new UserStatistic(rs.getString("photo_uri"), rs.getString("status"), timestamp)
        );
    }

    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM user");
    }

    /**
     * Только для тестов
     *
     * @param user
     */
    public void saveWithTimestamp(User user) {
        if (StringUtils.isEmpty(user.getId())) {
            user.setId(getNewUserId());
        }

        jdbcTemplate.update("insert into user(id, photo_uri, login, email, status, status_timestamp) values(?,?,?,?,?,?)",
                user.getId(), user.getPhotoUri(), user.getLogin(), user.getEmail(), user.getStatus(), user.getStatusTimestamp());
    }
}
