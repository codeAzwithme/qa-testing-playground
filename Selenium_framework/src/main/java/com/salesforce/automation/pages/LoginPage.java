package com.salesforce.automation.pages;

import java.time.Duration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private static final Duration EXPLICIT_WAIT = Duration.ofSeconds(20);

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//input[@id='username']")
    private WebElement usernameField;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@id='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//input[@id='rememberUn']")
    private WebElement rememberMeCheckbox;

    @FindBy(xpath = "//div[@id='error']")
    private WebElement errorMessage;

    @FindBy(xpath = "//a[@id='forgot_password_link']")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//a[@id='mydomainLink']")
    private WebElement customDomainLink;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, EXPLICIT_WAIT);
        PageFactory.initElements(driver, this);
    }

    public void enterUsername(String username) {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            usernameField.clear();
            usernameField.sendKeys(username);
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Username field is not available: " + exception.getMessage(), exception);
        }
    }

    public void enterPassword(String password) {
        try {
            wait.until(ExpectedConditions.visibilityOf(passwordField));
            passwordField.clear();
            passwordField.sendKeys(password);
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Password field is not available: " + exception.getMessage(), exception);
        }
    }

    public void clickLogin() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Login button is not clickable: " + exception.getMessage(), exception);
        }
    }

    public void setRememberMe(boolean enabled) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(rememberMeCheckbox));
            if (rememberMeCheckbox.isSelected() != enabled) {
                rememberMeCheckbox.click();
            }
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Remember me checkbox is not available: " + exception.getMessage(), exception);
        }
    }

    public boolean isRememberMeSelected() {
        try {
            wait.until(ExpectedConditions.visibilityOf(rememberMeCheckbox));
            return rememberMeCheckbox.isSelected();
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Remember me checkbox is not available: " + exception.getMessage(), exception);
        }
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getErrorMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText().trim();
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Error message was not displayed: " + exception.getMessage(), exception);
        }
    }

    public boolean isErrorMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.isDisplayed();
        } catch (TimeoutException | NoSuchElementException exception) {
            return false;
        }
    }

    public String getEnteredUsername() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            return usernameField.getAttribute("value");
        } catch (TimeoutException | NoSuchElementException exception) {
            throw new IllegalStateException("Username field is not available: " + exception.getMessage(), exception);
        }
    }

    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameField));
            return usernameField.isDisplayed()
                    && passwordField.isDisplayed()
                    && loginButton.isDisplayed()
                    && rememberMeCheckbox.isDisplayed();
        } catch (TimeoutException | NoSuchElementException exception) {
            return false;
        }
    }

    public boolean areSecondaryLinksDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(forgotPasswordLink));
            return forgotPasswordLink.isDisplayed() && customDomainLink.isDisplayed();
        } catch (TimeoutException | NoSuchElementException exception) {
            return false;
        }
    }

    public boolean isLoginButtonEnabled() {
        try {
            wait.until(ExpectedConditions.visibilityOf(loginButton));
            return loginButton.isEnabled();
        } catch (TimeoutException | NoSuchElementException exception) {
            return false;
        }
    }

    public boolean waitForUrlChange(String loginUrlFragment) {
        try {
            return wait.until(ExpectedConditions.not(ExpectedConditions.urlContains(loginUrlFragment)));
        } catch (TimeoutException exception) {
            return false;
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to evaluate current URL: " + exception.getMessage(), exception);
        }
    }

    public String getPageTitle() {
        try {
            return driver.getTitle();
        } catch (WebDriverException exception) {
            throw new IllegalStateException("Unable to read page title: " + exception.getMessage(), exception);
        }
    }
}
