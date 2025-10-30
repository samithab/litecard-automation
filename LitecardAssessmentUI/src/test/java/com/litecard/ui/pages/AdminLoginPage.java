package com.litecard.ui.pages;

import com.litecard.utils.ScreenshotUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdminLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AdminLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void open(String baseUrl) {
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://demo.litecard.io/";
        }
        driver.get(baseUrl);
        System.out.println("Navigated to: " + baseUrl);
    }

    public void login(String username, String password) throws InterruptedException {
        try {
            System.out.println("Starting login process for: " + username);

            // Step 1: Click "Log In"
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[normalize-space(text())='Log In' or normalize-space(text())='Login']")
            ));
            loginBtn.click();
            System.out.println("Clicked 'Log In' button on homepage. Waiting for Auth0 page...");

            // Step 2: Wait for Auth0
            wait.until(ExpectedConditions.urlContains("auth0.com"));
            System.out.println("Redirected to Auth0 login page: " + driver.getCurrentUrl());

            // Step 3: Fill credentials
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

            WebElement emailInput = driver.findElement(By.id("username"));
            WebElement passwordInput = driver.findElement(By.id("password"));

            emailInput.clear();
            emailInput.sendKeys(username);
            passwordInput.clear();
            passwordInput.sendKeys(password);
            System.out.println("✏Filled in credentials.");

            // Step 4: Submit
            WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit']")
            ));
            continueBtn.click();
            System.out.println("Submitted Auth0 form, waiting for redirect back to Litecard...");

            // Step 5: Validate success or failure
            boolean loginSuccessful = false;
            long start = System.currentTimeMillis();

            while (System.currentTimeMillis() - start < 15000) {
                if (driver.getCurrentUrl().contains("litecard.io")
                        && driver.getTitle().contains("View Passes")) {
                    loginSuccessful = true;
                    break;
                }

                if (driver.findElements(By.xpath("//*[contains(text(),'Dashboard') or contains(text(),'View Passes')]")).size() > 0) {
                    loginSuccessful = true;
                    break;
                }

                if (driver.findElements(By.xpath("//*[contains(text(),'Wrong email or password') or contains(text(),'invalid') or contains(text(),'Error')]")).size() > 0) {
                    throw new RuntimeException("Login failed: Invalid username or password.");
                }

                Thread.sleep(500);
            }

            if (!loginSuccessful) {
                throw new RuntimeException("Login failed or dashboard did not load within expected time!");
            }

            System.out.println("Successfully logged in to Litecard dashboard!");

        } catch (Exception e) {
            // Screenshot on ANY failure
            System.err.println("Login failed: " + e.getMessage());
            ScreenshotUtils.captureScreenshot(driver, "LoginFailure");
            throw e; // rethrow to let TestNG mark test as failed
        }
    }
}

