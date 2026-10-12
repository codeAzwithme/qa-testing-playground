package com.salesforce.automation.utils;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public final class DriverFactory {

    private static final Duration IMPLICIT_WAIT = Duration.ofSeconds(5);
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(40);

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--remote-allow-origins=*");
            if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
                options.addArguments("--headless=new");
                options.addArguments("--window-size=1920,1080");
            }
            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT);
            driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
            return driver;
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to initialise Chrome driver: " + exception.getMessage(), exception);
        }
    }

    public static void quitDriver(WebDriver driver) {
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
        } catch (WebDriverException exception) {
            System.err.println("Driver quit failed: " + exception.getMessage());
        }
    }
}
