package com.litecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtils {

    // Write email→cardId mappings into CSV
    public static void writeMapping(List<Pair<String, String>> mapping, Path outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("email", "cardId"))) {
            for (Pair<String, String> pair : mapping) {
                csvPrinter.printRecord(pair.getLeft(), pair.getRight());
            }
            csvPrinter.flush();
        }
        System.out.println("CSV report written to: " + outputFile.toAbsolutePath());
    }

    // Write email→cardId mappings into JSON
    public static void writeJsonReport(List<Pair<String, String>> mapping, Path outputFile) throws IOException {
        // Convert mapping to a structured list of objects
        List<MappingRecord> records = mapping.stream()
                .map(p -> new MappingRecord(p.getLeft(), p.getRight()))
                .collect(Collectors.toList());

        // Pretty-print JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonContent = gson.toJson(records);

        // Write JSON file
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            writer.write(jsonContent);
        }
        System.out.println("JSON report written to: " + outputFile.toAbsolutePath());
    }

    // Internal helper class for clean JSON structure
    private static class MappingRecord {
        String email;
        String cardId;

        MappingRecord(String email, String cardId) {
            this.email = email;
            this.cardId = cardId;
        }
    }
}
