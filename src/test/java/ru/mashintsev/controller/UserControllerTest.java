package ru.mashintsev.controller;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.MultiPartConfig;
import com.jayway.restassured.http.ContentType;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.mashintsev.Application;
import ru.mashintsev.dao.UserDAO;
import ru.mashintsev.domain.StatisticsParams;
import ru.mashintsev.domain.User;
import ru.mashintsev.domain.UserStatusParams;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by mashintsev@gmail.com on 17.08.15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class UserControllerTest {

    @Value("${local.server.port}")
    int port;

    @Autowired
    UserDAO userDAO;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }

    @Test
    public void uploadImage1Test() throws Exception {
        given().
                multiPart(new File("test/images/test1.jpeg"))
                .when()
                .post("/upload")
                .then()
                .body("success", is(true))
                .body("result", notNullValue(String.class));
    }

    @Test
    public void uploadImage2Test() throws Exception {
        given().
                multiPart(new File("test/images/test2.jpg"))
                .when()
                .post("/upload")
                .then()
                .body("success", is(true))
                .body("result", notNullValue(String.class));
    }

    @Test
    public void createUserTest() throws Exception {
        User u = new User();
        u.setEmail("test_1@te.com");
        u.setLogin("test_1");
        u.setStatus("Online");

        given().contentType("application/json")
                .body(u)
                .when().post("/user")
                .then().body("result.id", notNullValue(String.class));
    }

    @Test
    public void getNotExistingUserTest() throws Exception {
        when().get("/user/{userId}", "no-user")
                .then().body("success", is(false)).body("result", nullValue());
    }

    @Test
    public void getExistingUserTest() throws Exception {
        User u = new User();
        u.setId("test_2");
        u.setEmail("test_2@te.com");
        u.setLogin("test_2");
        u.setStatus("Online");
        userDAO.save(u);

        when().get("/user/{userId}", "test_2")
                .then().body("success", is(true)).body("result.id", is("test_2"));
    }

    @Test
    public void updateStatusUserTest() throws Exception {
        User u = new User();
        u.setId("test_3");
        u.setStatus("Online");
        userDAO.save(u);

        UserStatusParams params = new UserStatusParams();
        params.setUserId("test_3");
        params.setStatus("offline");

        given().contentType("application/json").body(params)
                .when().post("/userstatus")
                .then()
                .body("result", notNullValue())
                .body("result.userId", is("test_3"))
                .body("result.oldStatus", is("Online"))
                .body("result.newStatus", is("Offline"));
    }

    @Test
    public void updateStatusUserToBadStatusTest() throws Exception {
        User u = new User();
        u.setId("test_4");
        u.setStatus("Online");
        userDAO.save(u);

        UserStatusParams params = new UserStatusParams();
        params.setUserId("test_4");
        params.setStatus("off");

        given().contentType("application/json").body(params)
                .when().post("/userstatus")
                .then()
                .body("success", is(false))
                .body("result", nullValue());
    }

    @Test
    public void updateStatusUserToTheSameTest() throws Exception {
        User u = new User();
        u.setId("test_5");
        u.setStatus("Online");
        userDAO.save(u);

        UserStatusParams params = new UserStatusParams();
        params.setUserId("test_5");
        params.setStatus("online");

        given().contentType("application/json").body(params)
                .when().post("/userstatus")
                .then()
                .body("result", notNullValue())
                .body("result.userId", is("test_5"))
                .body("result.oldStatus", is("Online"))
                .body("result.newStatus", is("Online"));
    }

    @Test
    public void updateStatusNotExistingUserTest() throws Exception {
        UserStatusParams params = new UserStatusParams();
        params.setUserId("test_6");
        params.setStatus("offline");

        given().contentType("application/json").body(params)
                .when().post("/userstatus")
                .then()
                .body("success", is(false))
                .body("result", nullValue());
    }

    @Test
    public void getEmptyStatisticsTest() throws Exception {
        userDAO.deleteAll();

        StatisticsParams params = new StatisticsParams();
        params.setStatus("online");
        params.setTimestamp(new Date());

        given().contentType("application/json").body(params)
                .when().post("/statistics")
                .then()
                .body("result", notNullValue())
                .body("result", isA(List.class));
    }

    private void createUsersStatistics() throws ParseException {
        User u = new User();
        u.setId("test_stat_1");
        u.setStatus("Online");
        u.setStatusTimestamp(DateUtils.parseDate("2015-08-10 21:00:00,999", "yyyy-MM-dd HH:mm:ss,SSS"));
        userDAO.saveWithTimestamp(u);
        u = new User();
        u.setId("test_stat_2");
        u.setStatus("Online");
        u.setStatusTimestamp(DateUtils.parseDate("2015-08-11 21:00:00,999", "yyyy-MM-dd HH:mm:ss,SSS"));
        userDAO.saveWithTimestamp(u);
        u = new User();
        u.setId("test_stat_3");
        u.setStatus("Offline");
        u.setStatusTimestamp(DateUtils.parseDate("2015-08-12 21:00:00,999", "yyyy-MM-dd HH:mm:ss,SSS"));
        userDAO.saveWithTimestamp(u);
        u = new User();
        u.setId("test_stat_4");
        u.setStatus("Offline");
        u.setStatusTimestamp(DateUtils.parseDate("2015-08-13 21:00:00,999", "yyyy-MM-dd HH:mm:ss,SSS"));
        userDAO.saveWithTimestamp(u);
    }

    @Test
    public void getStatisticsForOnlineTest() throws Exception {
        userDAO.deleteAll();
        createUsersStatistics();

        StatisticsParams params = new StatisticsParams();
        params.setStatus("online");

        given().contentType("application/json").body(params)
                .when().post("/statistics")
                .then()
                .body("result", notNullValue())
                .body("result", isA(List.class))
                .body("result.size()", is(2));
    }

    @Test
    public void getStatisticsForOfflineTest() throws Exception {
        userDAO.deleteAll();

        createUsersStatistics();


        StatisticsParams params = new StatisticsParams();
        params.setStatus("offline");

        given().contentType("application/json").body(params)
                .when().post("/statistics")
                .then()
                .body("result", notNullValue())
                .body("result", isA(List.class))
                .body("result.size()", is(2));
    }

    @Test
    public void getStatisticsForTimestampAndOnlineTest() throws Exception {
        userDAO.deleteAll();
        createUsersStatistics();

        StatisticsParams params = new StatisticsParams();
        params.setStatus("online");
        params.setTimestamp(DateUtils.parseDate("2015-08-11 21:00:00,999", "yyyy-MM-dd HH:mm:ss,SSS"));

        given().contentType("application/json").body(params)
                .when().post("/statistics")
                .then()
                .body("result", notNullValue())
                .body("result", isA(List.class))
                .body("result.size()", is(1));
    }
}