package com.litecard.ui.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class AdminPassesPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public AdminPassesPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    // Open the "View Passes" page via side navigation
    public void openPasses() {
        try {
            // Wait for the dashboard to finish loading
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleContains("Litecard"),
                    ExpectedConditions.urlContains("litecard.io")
            ));

            // Wait for and click the “View Passes” menu item
            WebElement passesMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[@class='ant-menu-title-content' and normalize-space(text())='View Passes']")
            ));

            // Scroll into view and click safely
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", passesMenu);
            Thread.sleep(500);
            try {
                passesMenu.click();
            } catch (Exception e) {
                // fallback JS click if standard click fails
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", passesMenu);
            }

            System.out.println("Clicked 'View Passes' from sidebar navigation.");

            // Wait for title to confirm navigation
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.titleIs("View Passes | Litecard"),
                    ExpectedConditions.urlContains("/cards")
            ));
            System.out.println("Confirmed navigation to 'View Passes' page.");

            // Wait for the passes table or search bar to be visible
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='search']")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//table"))
            ));
        } catch (TimeoutException | InterruptedException e) {
            throw new RuntimeException("Failed to navigate to 'View Passes' page or load elements in time.", e);
        }
    }

    // Search for a pass by email and extract its Card ID
    public Optional<String> findCardIdByEmail(String email) {
        try {
            // Wait for table or search field to be ready
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("tr.ant-table-row.ant-table-row-level-0")
            ));

            // Find the visible search box (may not be interactable normally)
            WebElement searchInput = null;
            List<WebElement> possibleInputs = driver.findElements(By.cssSelector("input[type='search'], input[name='q'], input[name='search']"));
            for (WebElement el : possibleInputs) {
                if (el.isDisplayed()) {
                    searchInput = el;
                    break;
                }
            }

            if (searchInput == null) {
                throw new RuntimeException("No visible search input found on page!");
            }

            // Scroll and clear it via JS
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchInput);
            Thread.sleep(300);

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', { bubbles: true }));",
                    searchInput, email
            );
            System.out.println("Set search value via JS: " + email);

            // Wait for results to filter
            Thread.sleep(2000);

            // Fetch all visible rows
            List<WebElement> rows = driver.findElements(By.cssSelector("tr.ant-table-row.ant-table-row-level-0"));
            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.cssSelector("td.ant-table-cell"));
                if (cells.size() >= 4) {
                    String emailCell = cells.get(3).getText().trim();
                    if (emailCell.equalsIgnoreCase(email)) {
                        String cardId = cells.get(0).getText().trim();
                        System.out.println("Found cardId for " + email + ": " + cardId);
                        return Optional.of(cardId);
                    }
                }
            }

            System.out.println("No matching row found for " + email);
            return Optional.empty();

        } catch (Exception e) {
            throw new RuntimeException("findCardIdByEmail failed for " + email + ": " + e.getMessage(), e);
        }
    }
}
