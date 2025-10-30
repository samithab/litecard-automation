package com.litecard.ui.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opt = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) opt.addArguments("--headless=new");
        driver = new ChromeDriver(opt);
        driver.manage().window().setSize(new Dimension(1280, 1024));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) throws InterruptedException {
        Thread.sleep(1000); // give listener time to take screenshot
        if (driver != null) {
            driver.quit();
        }
    }
}

