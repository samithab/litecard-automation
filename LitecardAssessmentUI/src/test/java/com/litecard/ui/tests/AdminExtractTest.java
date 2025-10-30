package com.litecard.ui.tests;

import com.litecard.ui.pages.AdminLoginPage;
import com.litecard.ui.pages.AdminPassesPage;
import com.litecard.utils.CsvUtils;
import config.TestConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminExtractTest extends BaseTest {

    @Test
    public void extractCardIds() throws Exception {
        System.out.println("===Starting AdminExtractTest ===");

        // 1.Login to the Litecard dashboard through Auth0
        AdminLoginPage login = new AdminLoginPage(driver);
        login.open(TestConfig.props.getProperty("base.url"));
        login.login(
                TestConfig.props.getProperty("admin.username"),
                TestConfig.props.getProperty("admin.password")
        );

        // 2.Once logged in, open the Passes page or section
        AdminPassesPage passes = new AdminPassesPage(driver);
        passes.openPasses();
        System.out.println("Opened Passes section in dashboard.");

        // 3.Load previously created email list
        List<String> emails = Files.readAllLines(Paths.get("target/emails_created.csv"))
                .stream()
                .skip(1) // skip header row
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        System.out.println("Loaded " + emails.size() + " emails from target/emails_created.csv");

        List<Pair<String, String>> mapping = new ArrayList<>();

        // 4.For each email, find its corresponding cardId in the dashboard
        for (String email : emails) {
            System.out.println("Searching for cardId linked to: " + email);

            Optional<String> cardId = passes.findCardIdByEmail(email);

            if (cardId.isPresent()) {
                //System.out.println("Found cardId for " + email + ": " + cardId.get());
                mapping.add(Pair.of(email, cardId.get()));
            } else {
                System.out.println("NOT FOUND: " + email);
            }
        }

        // 5.Save mappings to CSV
        String outputPath = TestConfig.props.getProperty("output.file", "target/card_mapping.csv");
        CsvUtils.writeMapping(mapping, Paths.get(outputPath));
        System.out.println("Saved " + mapping.size() + " mappings to " + outputPath);

        // Also write JSON report
        Path jsonOut = Paths.get(TestConfig.props.getProperty("output.json"));
        CsvUtils.writeJsonReport(mapping, jsonOut);


        // 6.Assert expected number of mappings
        Assert.assertEquals(mapping.size(), emails.size(),
                "Expected all created passes to be mapped with cardIds.");

        System.out.println("AdminExtractTest completed successfully.");
    }
}
