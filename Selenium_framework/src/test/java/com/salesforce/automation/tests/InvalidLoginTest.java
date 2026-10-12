package com.salesforce.automation.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InvalidLoginTest extends BaseTest {

    private static final String CREDENTIAL_ERROR_FRAGMENT = "check your username and password";
    private static final String EMPTY_USERNAME_ERROR_FRAGMENT = "enter your username";
    private static final String EMPTY_PASSWORD_ERROR_FRAGMENT = "enter your password";

    @DataProvider(name = "rejectedCredentials")
    public Object[][] rejectedCredentials() {
        return new Object[][] {
                {"invalid.user@example.com", "WrongPass@123", CREDENTIAL_ERROR_FRAGMENT},
                {"invalid.user@example.com", "Wrong Pass With Spaces", CREDENTIAL_ERROR_FRAGMENT},
                {"not-an-email", "WrongPass@123", CREDENTIAL_ERROR_FRAGMENT},
                {"' OR '1'='1", "' OR '1'='1", CREDENTIAL_ERROR_FRAGMENT},
                {"<script>alert(1)</script>", "WrongPass@123", CREDENTIAL_ERROR_FRAGMENT},
                {"   ", "WrongPass@123", EMPTY_USERNAME_ERROR_FRAGMENT}
        };
    }

    @Test(dataProvider = "rejectedCredentials", priority = 1,
            description = "Invalid credential combinations are rejected with an error")
    public void verifyLoginIsRejectedForInvalidCredentials(String username, String password, String expectedFragment) {
        try {
            loginPage.login(username, password);
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed");
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(expectedFragment),
                    "Unexpected error message: " + loginPage.getErrorMessage());
            Assert.assertTrue(driver.getCurrentUrl().contains(LOGIN_URL_FRAGMENT), "User left the login page");
        } catch (IllegalStateException exception) {
            Assert.fail("Invalid login verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 2, description = "Empty username is rejected")
    public void verifyLoginWithEmptyUsername() {
        try {
            loginPage.enterPassword("WrongPass@123");
            loginPage.clickLogin();
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed");
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(EMPTY_USERNAME_ERROR_FRAGMENT),
                    "Unexpected error message: " + loginPage.getErrorMessage());
        } catch (IllegalStateException exception) {
            Assert.fail("Empty username verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 3, description = "Empty password is rejected")
    public void verifyLoginWithEmptyPassword() {
        try {
            loginPage.enterUsername("invalid.user@example.com");
            loginPage.clickLogin();
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed");
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(EMPTY_PASSWORD_ERROR_FRAGMENT),
                    "Unexpected error message: " + loginPage.getErrorMessage());
        } catch (IllegalStateException exception) {
            Assert.fail("Empty password verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 4, description = "Empty username and password are rejected")
    public void verifyLoginWithEmptyCredentials() {
        try {
            loginPage.clickLogin();
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed");
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(EMPTY_USERNAME_ERROR_FRAGMENT),
                    "Unexpected error message: " + loginPage.getErrorMessage());
        } catch (IllegalStateException exception) {
            Assert.fail("Empty credentials verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 5, description = "Invalid login with Remember me selected is still rejected")
    public void verifyInvalidLoginWithRememberMe() {
        try {
            loginPage.setRememberMe(true);
            loginPage.login("invalid.user@example.com", "WrongPass@123");
            Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message was not displayed");
            Assert.assertTrue(loginPage.getErrorMessage().toLowerCase().contains(CREDENTIAL_ERROR_FRAGMENT),
                    "Unexpected error message: " + loginPage.getErrorMessage());
        } catch (IllegalStateException exception) {
            Assert.fail("Remember me invalid login verification failed: " + exception.getMessage());
        }
    }
}
