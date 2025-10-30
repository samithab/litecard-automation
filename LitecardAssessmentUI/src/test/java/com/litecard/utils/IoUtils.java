package com.litecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class IoUtils {
    public static void writeCsv(Path path, List<String[]> rows) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toFile()))) {
            for (String[] r : rows) writer.writeNext(r);
        }
    }

    public static void writeJson(Path path, Object obj) throws IOException {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter fw = new FileWriter(path.toFile())) {
            fw.write(g.toJson(obj));
        }
    }
}
