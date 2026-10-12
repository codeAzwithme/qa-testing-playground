package com.salesforce.automation.tests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class ValidLoginTest extends BaseTest {

    private static String property(String key) {
        String value = System.getProperty(key);
        return value == null ? "" : value.trim();
    }

    @Test(priority = 1, description = "Login page renders all mandatory elements")
    public void verifyLoginPageElementsAreDisplayed() {
        try {
            Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login form elements are not displayed");
            Assert.assertTrue(loginPage.areSecondaryLinksDisplayed(), "Secondary links are not displayed");
            Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button is not enabled");
            Assert.assertTrue(driver.getCurrentUrl().contains(LOGIN_URL_FRAGMENT), "Unexpected login URL");
            Assert.assertFalse(loginPage.getPageTitle().isEmpty(), "Page title is empty");
        } catch (IllegalStateException exception) {
            Assert.fail("Login page verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 2, description = "Remember me checkbox can be selected and cleared")
    public void verifyRememberMeToggle() {
        try {
            Assert.assertFalse(loginPage.isRememberMeSelected(), "Remember me should be unchecked by default");
            loginPage.setRememberMe(true);
            Assert.assertTrue(loginPage.isRememberMeSelected(), "Remember me was not selected");
            loginPage.setRememberMe(false);
            Assert.assertFalse(loginPage.isRememberMeSelected(), "Remember me was not cleared");
        } catch (IllegalStateException exception) {
            Assert.fail("Remember me verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 3, description = "Username is retained when typed into the form")
    public void verifyUsernameInputIsAccepted() {
        String username = "qa.tester@example.com";
        try {
            loginPage.enterUsername(username);
            loginPage.enterPassword("Sample@123");
            Assert.assertEquals(loginPage.getEnteredUsername(), username, "Username value was not retained");
        } catch (IllegalStateException exception) {
            Assert.fail("Username input verification failed: " + exception.getMessage());
        }
    }

    @Test(priority = 4, description = "Valid credentials navigate away from the login page")
    public void verifyLoginWithValidCredentials() {
        String username = property("sf.user");
        String password = property("sf.pass");
        if (username.isEmpty() || password.isEmpty()) {
            throw new SkipException("Valid credentials not supplied via -Dsf.user and -Dsf.pass");
        }
        try {
            loginPage.setRememberMe(true);
            loginPage.login(username, password);
            Assert.assertTrue(loginPage.waitForUrlChange(LOGIN_URL_FRAGMENT),
                    "Login did not navigate away from the login page");
            Assert.assertFalse(loginPage.isErrorMessageDisplayed(), "Unexpected error shown for valid credentials");
        } catch (IllegalStateException exception) {
            Assert.fail("Valid login failed: " + exception.getMessage());
        }
    }
}
