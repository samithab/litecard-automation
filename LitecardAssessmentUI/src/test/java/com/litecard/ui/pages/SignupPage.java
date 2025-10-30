package com.litecard.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignupPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /** Opens the signup form URL */
    public void open(String url) {
        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    /**
     * Fills the signup form (First Name, Last Name, Email, Birthday),
     * ticks the checkbox, and clicks the Submit button.
     */
    public void fillAndSubmit(String firstName, String lastName, String email, String birthday) {
        try {
            // Fill First Name
            WebElement firstNameInput = findInputByNameOrPlaceholder("first", "First Name");
            firstNameInput.clear();
            firstNameInput.sendKeys(firstName);

            // Fill Last Name
            WebElement lastNameInput = findInputByNameOrPlaceholder("last", "Last Name");
            lastNameInput.clear();
            lastNameInput.sendKeys(lastName);

            // Fill Email
            WebElement emailInput = findEmailField();
            emailInput.clear();
            emailInput.sendKeys(email);

            // Fill Birthday (ISO date or plain yyyy-MM-dd)
            WebElement birthdayInput = findInputByNameOrPlaceholder("birth", "Birthday");
            birthdayInput.clear();
            birthdayInput.sendKeys(birthday);

            // Check the required checkbox (consent / terms)
            tickCheckbox();

            // Click submit
            clickSubmit();

        } catch (Exception e) {
            throw new RuntimeException("Failed to fill and submit form: " + e.getMessage(), e);
        }
    }

    /** Find text input by name or placeholder keyword */
    private WebElement findInputByNameOrPlaceholder(String nameHint, String placeholderHint) {
        By locator = By.xpath(String.format(
                "//input[contains(translate(@name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'%s') " +
                        "or contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'%s')]",
                nameHint.toLowerCase(), placeholderHint.toLowerCase()));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Find email field */
    private WebElement findEmailField() {
        By locator = By.xpath("//input[@type='email' or contains(@name,'mail')]");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Tick the checkbox if not already checked */
    private void tickCheckbox() {
        try {
            By checkboxLocator = By.cssSelector("span.ant-checkbox-inner");

            // Wait until checkbox is visible in DOM
            WebElement checkbox = wait.until(ExpectedConditions.visibilityOfElementLocated(checkboxLocator));

            // Scroll slightly to make sure it's not overlapped by form fields
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", checkbox);
            Thread.sleep(500); // Allow React layout to stabilize

            // Extra safety: wait for clickable state (helps with overlay animations)
            wait.until(ExpectedConditions.elementToBeClickable(checkbox));

            // Use JavaScript click (bypasses intercepted click issue)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);

            System.out.println("Checkbox clicked successfully (ant-checkbox-inner, via JS).");

        } catch (Exception e) {
            throw new RuntimeException("Failed to tick checkbox reliably: " + e.getMessage(), e);
        }
    }

    /** Clicks the Submit button */
    private void clickSubmit() {
        try {
            // Try button[type='submit']
            if (!driver.findElements(By.cssSelector("button[type='submit']")).isEmpty()) {
                driver.findElement(By.cssSelector("button[type='submit']")).click();
                return;
            }

            // Try any button with text like 'Submit' or 'Sign'
            for (WebElement b : driver.findElements(By.tagName("button"))) {
                String txt = b.getText().toLowerCase();
                if (txt.contains("submit") || txt.contains("sign") || txt.contains("create") || txt.contains("join")) {
                    b.click();
                    return;
                }
            }

            // Fallback: submit form directly
            driver.findElement(By.tagName("form")).submit();

        } catch (Exception e) {
            throw new RuntimeException("Could not click submit button: " + e.getMessage(), e);
        }
    }

    /**
     * Waits for the Add to Wallet image (Apple or Google) after successful submission.
     * Returns true if found within timeout.
     */
    public boolean walletButtonsVisible() {
        try {
            By walletLocator = By.xpath(
                    "//img[contains(@alt,'Wallet') or contains(@src,'wallet') or " +
                            "contains(translate(@alt,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'apple') or " +
                            "contains(translate(@alt,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'google')]"
            );

            WebElement walletBtn = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(walletLocator));

            System.out.println("Wallet buttons found: ");
            return walletBtn.isDisplayed();

        } catch (TimeoutException e) {
            System.out.println("Wallet buttons not found within timeout.");
            return false;
        }
    }
}
