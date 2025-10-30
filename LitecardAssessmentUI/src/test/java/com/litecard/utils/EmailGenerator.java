package com.litecard.utils;

public class EmailGenerator {
    public static String generate(String lastName, int index) {
        return String.format("qa.%s.%d@example.com", lastName.toLowerCase(), index);
    }
}

