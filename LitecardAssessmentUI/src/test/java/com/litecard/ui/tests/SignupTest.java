package com.litecard.ui.tests;

import com.litecard.ui.pages.SignupPage;
import com.litecard.utils.EmailGenerator;
import config.TestConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SignupTest extends BaseTest {

    @Test
    public void createMultiplePasses() throws Exception {
        SignupPage signup = new SignupPage(driver);
        String formUrl = TestConfig.props.getProperty("signup.form.url");

        List<Pair<String, String>> emailToCard = new ArrayList<>();
        String lastName = "Sulakkana"; // change if needed

        for (int i = 1; i <= 10; i++) {
            String email = EmailGenerator.generate(lastName, i);
            signup.open(formUrl);

            System.out.println("Submitting form for: " + email);

            // Fill and submit required fields (birthday format yyyy-MM-dd)
            signup.fillAndSubmit("QA" + i, lastName, email, "30/01/1998");

            // Verify wallet buttons appear
            boolean walletVisible = signup.walletButtonsVisible();
            Assert.assertTrue(walletVisible, "Expected wallet buttons after signup for " + email);

            // Store created email
            emailToCard.add(Pair.of(email, ""));

            // Wait before next iteration
            Thread.sleep(1500);
        }

        // Save all emails for admin extraction
        Path out = Paths.get("target/emails_created.csv");
        try (BufferedWriter writer = Files.newBufferedWriter(out)) {
            writer.write("email\n");
            for (Pair<String, String> pair : emailToCard) {
                writer.write(pair.getLeft() + "\n");
            }
        }

        System.out.println("Created " + emailToCard.size() + " passes. Emails saved to: " + out);
    }
}
