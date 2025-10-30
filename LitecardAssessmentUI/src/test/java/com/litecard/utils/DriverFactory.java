package com.litecard.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;

public class DriverFactory {
    public static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        if (headless) {
            opts.addArguments("--headless=new");
            opts.addArguments("--window-size=1920,1080");
        } else {
            opts.addArguments("--start-maximized");
        }
        opts.addArguments("--disable-gpu");
        opts.addArguments("--no-sandbox");
        return new ChromeDriver(opts);
    }
}
