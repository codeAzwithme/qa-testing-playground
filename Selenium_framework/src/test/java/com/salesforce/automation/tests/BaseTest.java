package com.salesforce.automation.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import com.salesforce.automation.pages.LoginPage;
import com.salesforce.automation.utils.DriverFactory;

public abstract class BaseTest {

    protected static final String LOGIN_URL = "https://login.salesforce.com/?locale=in";
    protected static final String LOGIN_URL_FRAGMENT = "login.salesforce.com";

    protected WebDriver driver;
    protected LoginPage loginPage;

    @BeforeTest(alwaysRun = true)
    public void setUpDriver() {
        driver = DriverFactory.createDriver();
    }

    @BeforeMethod(alwaysRun = true)
    public void openLoginPage() {
        try {
            driver.manage().deleteAllCookies();
            driver.get(LOGIN_URL);
            loginPage = new LoginPage(driver);
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to open login page: " + exception.getMessage(), exception);
        }
    }

    @AfterTest(alwaysRun = true)
    public void tearDownDriver() {
        DriverFactory.quitDriver(driver);
    }
}
