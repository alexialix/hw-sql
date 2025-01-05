package ru.netology.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.equalTo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BankLoginTest {

    @AfterEach
    void teardown() {
        SQLHelper.cleanDatabase();
    }

    @Test
    void shouldSuccessfulLogin() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    @Test
    void shouldGetErrorIfLoginWithRandomUserWithoutAddingToBase() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotificationVisibility();
    }

    @Test
    void shouldGetErrorIfLoginWithExistUserAndRandomVerificationCode() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();

        var randomCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(randomCode.getCode());
        verificationPage.verifyErrorNotificationVisiblity();
    }

    @Test
    void shouldLockUserAfterThreeWrongAttempts() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();

        for (int i = 0; i < 3; i++) {
            var randomCode = DataHelper.generateRandomVerificationCode();
            verificationPage.verify(randomCode.getCode());
            verificationPage.verifyErrorNotificationVisiblity();
        }

        verificationPage.verifyErrorNotificationVisiblity();
    }
}
//    @Test
//    public void DatabaseConnection() throws SQLException {
//        String url = "jdbc:mysql://localhost:3306/app";
//        String user = "app";
//        String password = "pass";
//
//        try (Connection connection = DriverManager.getConnection(url, user, password)) {
//            assertNotNull(connection, "Connection should not be null");
//        }
//    }
//
//    @Test
//    public void SuccessfulLogin() {
//        String username = "app";
//        String password = "pass";
//
//        given()
//                .formParam("username", username)
//                .formParam("password", password)
//                .when()
//                .log().all()
//                .post("http://localhost:9999")
//                .then()
//                .statusCode(200)
//                .body("message", equalTo("Login successful"));
//    }
//
//    @Test
//    public void testAccountLockAfterThreeFailedAttempts() {
//        String username = "app";
//        String wrongPassword = "wrong_password";
//
//        for (int i = 0; i < 3; i++) {
//            given()
//                    .formParam("username", username)
//                    .formParam("password", wrongPassword)
//                    .when()
//                    .post("http://localhost:9999/login")
//                    .then()
//                    .statusCode(401)
//                    .body("message", equalTo("Invalid credentials"));
//        }
//
//        given()
//                .formParam("username", username)
//                .formParam("password", wrongPassword)
//                .when()
//                .post("http://localhost:9999/login")
//                .then()
//                .statusCode(403)
//                .body("message", equalTo("Account locked"));
//    }
//}


